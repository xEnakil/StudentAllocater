package com.university.StudentDropper.service;

import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.model.StudentAllocation;
import com.university.StudentDropper.repository.StudentAllocationRepository;
import com.university.StudentDropper.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class AllocationService {
    private final StudentRepository studentRepository;
    private final StudentAllocationRepository studentAllocationRepository;

    public AllocationService(StudentAllocationRepository studentAllocationRepository, StudentRepository studentRepository) {
        this.studentAllocationRepository = studentAllocationRepository;
        this.studentRepository = studentRepository;
    }

    public StudentAllocation getStudentAllocation(Student student) {
        return studentAllocationRepository.findByStudent(student);
    }
}
