package com.university.StudentDropper.repository;

import com.university.StudentDropper.model.Preference;
import com.university.StudentDropper.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    void deleteByStudentId(Long studentId);
    List<Preference> findByStudent(Student student);
}
