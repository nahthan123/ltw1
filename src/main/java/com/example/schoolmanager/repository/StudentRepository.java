package com.example.schoolmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schoolmanager.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByNameContainingIgnoreCase(String name);
}
