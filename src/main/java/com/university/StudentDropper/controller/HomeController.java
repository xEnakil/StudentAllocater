package com.university.StudentDropper.controller;

import com.university.StudentDropper.repository.StudentRepository;
import com.university.StudentDropper.util.AccountInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private final StudentRepository studentRepository;

    public HomeController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/menu")
    public String menu(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        return "menu";
    }
}
