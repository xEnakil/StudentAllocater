package com.university.StudentDropper.service;

import com.university.StudentDropper.dto.PreferenceDTO;
import com.university.StudentDropper.model.Preference;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.DestinationRepository;
import com.university.StudentDropper.repository.PreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PreferenceService {

    private final DestinationRepository destinationRepository;
    private final PreferenceRepository preferenceRepository;

    public PreferenceService(DestinationRepository destinationRepository, PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
        this.destinationRepository = destinationRepository;
    }

    public void savePreferences (Student student, List<PreferenceDTO> preferenceDTOS) {
        log.info("Saving preferences for student: {}", student.getName());
        if (hasSubmittedPreferences(student)) {
            throw new IllegalStateException("Preferences have already been submitted.");
        }

        List<Preference> preferences = new ArrayList<>();

        for (PreferenceDTO preferenceDTO : preferenceDTOS) {
            Preference preference = new Preference();
            preference.setStudent(student);
            preference.setDestination(destinationRepository.findById(preferenceDTO.getDestinationId())
                            .orElseThrow(() -> new EntityNotFoundException("Destination not found")));
            preference.setRank(preferenceDTO.getRanking());
            preferences.add(preference);
        }

        preferenceRepository.saveAll(preferences);
    }

    public List<PreferenceDTO> findPreferencesByStudent(Student student) {
        log.info("Finding preferences for student: {}", student.getName());
        List<Preference> preferences = preferenceRepository.findByStudent(student);
        return preferences.stream()
                .map(preference -> {
                    PreferenceDTO dto = new PreferenceDTO();
                    dto.setDestinationName(preference.getDestination().getName());
                    dto.setRanking(preference.getRank());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void updatePreferences(Student student, List<PreferenceDTO> preferenceDTOs) {
        preferenceRepository.deleteByStudentId(student.getId());
        savePreferences(student, preferenceDTOs);
    }

    public boolean hasSubmittedPreferences(Student student) {
        List<Preference> preferences = preferenceRepository.findByStudent(student);
        return preferences != null && !preferences.isEmpty();
    }
}
