package com.university.StudentDropper.util;

import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.repository.StudentRepository;
import org.springframework.ui.Model;

import java.security.Principal;

public class AccountInfo {


    public static void showAccount(Model model, Principal principal, StudentRepository studentRepository) {
        if (principal != null) {
            String email = principal.getName();
            Student student = studentRepository.findByEmail(email);
            if (student != null) {
                model.addAttribute("studentName", student.getName());
                model.addAttribute("studentSurname", student.getSurname());
            }
        }
    }
}
