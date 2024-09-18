package com.university.StudentDropper.controller;

import com.university.StudentDropper.dto.AllDTO;
import com.university.StudentDropper.exception.InsufficientStudentsException;
import com.university.StudentDropper.repository.StudentRepository;
import com.university.StudentDropper.service.AllocationService;
import com.university.StudentDropper.service.GeneticAlgorithmService;
import com.university.StudentDropper.service.AdminService;
import com.university.StudentDropper.util.AccountInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final GeneticAlgorithmService geneticAlgorithmService;
    private final AdminService adminService;
    private final StudentRepository studentRepository;
    private final AllocationService allocationService;

    @GetMapping("/menu")
    public String adminMenu(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        return "admin_menu";
    }

    @GetMapping("/start-allocation")
    public String startAllocation(Model model,  Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);

        try {
            try {
                geneticAlgorithmService.startAllocationProcess();
                model.addAttribute("message", "Allocation process finished successfully!");
                log.info("Allocation started");
            } catch (Exception e) {
                model.addAttribute("message", "Allocation process failed: " + e.getMessage());
                log.error("Allocation failed", e);
            }
        } catch (IllegalStateException |InsufficientStudentsException e) {
            model.addAttribute("message", e.getMessage());
            log.error("Allocation failed", e);
        }
        return "admin_menu";
    }

    @GetMapping("/add-10-students")
    public String add10Students(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        try {
            adminService.generateStudents(10, false, false);
            model.addAttribute("message", "10 Student added without preferences successfully!");
        } catch ( Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "admin_menu";
    }

    @GetMapping("/add-10-students-with-ordered-preference")
    public String add10StudentsWithOrderedPreference(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        try {
            adminService.generateStudents(10, true, false);
            model.addAttribute("message", "10 Student added with ordered (1,2,3,4) preferences successfully!");
        } catch ( Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "admin_menu";
    }

    @GetMapping("/add-39-students-with-ordered-preference")
    public String add39StudentsWithOrderedPreference(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        try {
            adminService.generateStudents(39, true, false);
            model.addAttribute("message", "39 Student added with ordered (1,2,3,4) preferences successfully!");
        } catch ( Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "admin_menu";
    }

    @GetMapping("/add-39-students-with-random-preference")
    public String add39StudentsWithRandomPreference(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);
        try {
            adminService.generateStudents(39, true, true);
            model.addAttribute("message", "39 Student added with random (1-4) preferences successfully!");
        } catch ( Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "admin_menu";
    }



    @GetMapping("/get-students")
    public String getStudents(Model model, Principal principal) {
        AccountInfo.showAccount(model, principal, studentRepository);

        List<AllDTO> allStudents = adminService.getAllStudents();
        model.addAttribute("allStudents", allStudents);
        return "admin_menu_all_students";
    }
}
