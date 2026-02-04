package com.example.schoolmanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.schoolmanager.model.Student;
import com.example.schoolmanager.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    // Lấy tất cả sinh viên
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Thêm / sửa sinh viên
    public Student save(Student student) {
        return repository.save(student);
    }

    // Xóa sinh viên
    public void delete(int id) {
        repository.deleteById(id);
    }

    // Tìm theo ID
    public Student getStudentById(int id) {
        return repository.findById(id).orElse(null);
    }

    // Tìm theo tên
    public List<Student> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
}
