package com.university.StudentDropper.controller;

import com.university.StudentDropper.dto.StudentDTO;
import com.university.StudentDropper.repository.StudentRepository;
import com.university.StudentDropper.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class AuthController {
    private final StudentRepository studentRepository;

    private final AuthService authService;

    public AuthController(AuthService authService,
                          StudentRepository studentRepository) {
        this.authService = authService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("message", "Welcome to our Thymeleaf example!");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@ModelAttribute("student") StudentDTO studentDTO, Model model) {
        long studentCount = studentRepository.count();

        if (studentCount >= 40) {
            model.addAttribute("message", "Registration is closed. The maximum number of students has been reached.");
            return "register";
        }

            try {
            authService.registerStudent(studentDTO);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "register";
        }
    }
}
