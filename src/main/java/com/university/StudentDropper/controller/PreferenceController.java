package com.university.StudentDropper.controller;


import com.university.StudentDropper.dto.PreferenceDTO;
import com.university.StudentDropper.dto.PreferenceFormDTO;
import com.university.StudentDropper.model.Destination;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.DestinationRepository;
import com.university.StudentDropper.repository.StudentRepository;
import com.university.StudentDropper.service.PreferenceService;
import com.university.StudentDropper.util.AccountInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PreferenceController {

    private final StudentRepository studentRepository;
    private final PreferenceService preferenceService;
    private final DestinationRepository destinationRepository;

    @GetMapping("/submitPreference")
    public String submitPreference(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);

        List<Destination> destinations = destinationRepository.findAll();
        List<PreferenceDTO> preferenceDTOs = destinations.stream()
                .map(dest -> new PreferenceDTO(dest.getId(), 0))
                .collect(Collectors.toList());

        PreferenceFormDTO preferenceFormDTO = new PreferenceFormDTO(preferenceDTOs);
        model.addAttribute("destinations", destinations);
        model.addAttribute("preferenceFormDTO", preferenceFormDTO);
        return "submit_preference";
    }

    @PostMapping("/submitPreference")
    public String submitPreference(@ModelAttribute PreferenceFormDTO formDTO, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Student student = studentRepository.findByEmail(email);
        try {
            if (student != null && formDTO != null && formDTO.getPreferences() != null) {
                preferenceService.savePreferences(student, formDTO.getPreferences());
                redirectAttributes.addFlashAttribute("successMessage", "Preferences submitted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "There was a problem submitting your preferences.");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/menu";
    }

    @GetMapping("/myPreferences")
    public String myPreferences(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);

        Student student = studentRepository.findByEmail(principal.getName());
        if (student != null) {
            List<PreferenceDTO> myPreferences = preferenceService.findPreferencesByStudent(student);
            model.addAttribute("myPreferences", myPreferences);
        } else {
            model.addAttribute("errorMessage", "No student found with the provided credentials.");
        }

        List<Destination> destinations = destinationRepository.findAll();
        model.addAttribute("destinations", destinations);
        return "my_preferences";
    }
}
