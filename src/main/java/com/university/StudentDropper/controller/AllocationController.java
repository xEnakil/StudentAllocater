package com.university.StudentDropper.controller;

import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.model.StudentAllocation;
import com.university.StudentDropper.repository.StudentRepository;
import com.university.StudentDropper.service.AllocationService;
import com.university.StudentDropper.util.AccountInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AllocationController {

    private final StudentRepository studentRepository;
    private final AllocationService allocationService;

    public AllocationController(StudentRepository studentRepository, AllocationService allocationService) {
        this.studentRepository = studentRepository;
        this.allocationService = allocationService;
    }

    @GetMapping("/result")
    public String showAllocationResult(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);

        Student student = studentRepository.findByEmail(principal.getName());
        StudentAllocation allocation = allocationService.getStudentAllocation(student);
        model.addAttribute("allocation", allocation);
        return "result";
    }
}
