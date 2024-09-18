package com.university.StudentDropper.repository;

import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.model.StudentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAllocationRepository extends JpaRepository<StudentAllocation, Integer> {
    StudentAllocation findByStudent(Student student);
}
