package com.university.StudentDropper.service;

import com.university.StudentDropper.dto.AllDTO;
import com.university.StudentDropper.model.Destination;
import com.university.StudentDropper.model.Preference;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.model.StudentAllocation;
import com.university.StudentDropper.repository.DestinationRepository;
import com.university.StudentDropper.repository.PreferenceRepository;
import com.university.StudentDropper.repository.StudentAllocationRepository;
import com.university.StudentDropper.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AdminService {

    private static final int MAX_STUDENTS = 40;


    private final DataFactory dataFactory;
    private final Random random;
    private final StudentRepository studentRepository;
    private final PreferenceRepository preferenceRepository;
    private final DestinationRepository destinationRepository;
    private final StudentAllocationRepository studentAllocationRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(DataFactory dataFactory,
                        Random random,
                        PreferenceRepository preferenceRepository,
                        StudentRepository studentRepository,
                        PasswordEncoder passwordEncoder,
                        DestinationRepository destinationRepository,
                        StudentAllocationRepository studentAllocationRepository) {
        this.dataFactory = dataFactory;
        this.random = random;
        this.preferenceRepository = preferenceRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.destinationRepository = destinationRepository;
        this.studentAllocationRepository = studentAllocationRepository;
    }

    public void generateStudents(int numberOfStudents, boolean withPreference, boolean randomPreference) {
        long currentCount = studentRepository.count();
        if (currentCount + numberOfStudents > MAX_STUDENTS) {
            throw new IllegalStateException("Adding " + numberOfStudents + " students would exceed the maximum limit of " + MAX_STUDENTS);
        }

        List<Destination> destinations = withPreference ? destinationRepository.findAll() : null;
        if (withPreference && (destinations == null || destinations.size() < 4)) {
            throw new IllegalStateException("There must be at least 4 destinations to assign preferences.");
        }

        for (int i = 0; i < numberOfStudents; i++) {
            createStudent(withPreference, destinations, randomPreference);
        }
    }

    public void createStudent(boolean withPreference, List<Destination> destinations, boolean randomPreference) {
        String firstName = dataFactory.getFirstName();
        String lastName = dataFactory.getLastName();
        String email = dataFactory.getEmailAddress();
        String password = passwordEncoder.encode("1234");
        Integer age = random.nextInt(35 - 18 + 1) + 18;

        Student student = new Student(null, firstName, lastName, age, email, password, "ROLE_USER", null);
        studentRepository.save(student);

        if (randomPreference) {
            assignRandomPreferences(student, destinations);
        }

        if (withPreference) {
            assignPreferences(student, destinations);
        }
    }

    public void assignPreferences(Student student, List<Destination> destinations) {
        for (int rank = 1; rank <= 4; rank++) {
            Preference preference = new Preference(null, student, destinations.get(rank - 1), rank);
            preferenceRepository.save(preference);
        }
    }

    public void assignRandomPreferences(Student student, List<Destination> destinations) {
        for (int rank = 1; rank <= 4; rank++) {
            Collections.shuffle(destinations);
            Preference preference = new Preference(null, student, destinations.get(rank - 1), rank);
            preferenceRepository.save(preference);
        }
    }

    public boolean isDatabaseEmpty() {
        return studentRepository.count() == 0;
    }

    public List<AllDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> {
            AllDTO allDTO = new AllDTO();
            allDTO.setStudentName(student.getName());
            allDTO.setStudentSurname(student.getSurname());

            Preference topPreference = student.getPreferences().stream()
                    .min(Comparator.comparingInt(Preference::getRank))
                    .orElse(null);

            if (topPreference != null) {
                allDTO.setPreferredDestinationName(topPreference.getDestination().getName());
                allDTO.setPreferenceRank(topPreference.getRank());
            }

            StudentAllocation allocation = studentAllocationRepository.findByStudent(student);

            if (allocation != null) {
                allDTO.setAllocatedDestinationName(allocation.getAllocatedDestination());
                allDTO.setAllocatedRank(allocation.getRank());
            }

            return allDTO;
        }).toList();
    }
}
