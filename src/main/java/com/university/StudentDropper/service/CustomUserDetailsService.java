package com.university.StudentDropper.service;

import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new UsernameNotFoundException("Student not found with email: " + email);
        }

        String role = student.getRole();
        if (role == null || role.trim().isEmpty()) {
            log.error("Role is null or empty for student: {}", email);
            throw new UsernameNotFoundException("No role assigned for user with email: " + email);
        }

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .authorities(student.getRole())
                .build();
    }
}
