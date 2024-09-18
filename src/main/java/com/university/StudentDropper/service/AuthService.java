package com.university.StudentDropper.service;

import com.university.StudentDropper.dto.StudentDTO;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.StudentRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerStudent(StudentDTO studentDTO) {
        if (emailExists(studentDTO.getEmail())) {
            throw new EntityExistsException("Student with this email already exists");
        }

        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setSurname(studentDTO.getSurname());
        student.setAge(studentDTO.getAge());
        student.setEmail(studentDTO.getEmail());
        student.setRole("ROLE_USER");
        student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));

        studentRepository.save(student);
        log.info("Student registered successfully with name {}", studentDTO.getName());
    }

    private boolean emailExists(String email) {
        return studentRepository.findByEmail(email) != null;
    }
}
