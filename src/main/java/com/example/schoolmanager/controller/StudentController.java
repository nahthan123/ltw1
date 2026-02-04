package com.example.schoolmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.schoolmanager.model.Student;
import com.example.schoolmanager.service.StudentService;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService service;

    // 1. Thêm sinh viên
    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return service.save(student);
    }

    // 2. Xóa sinh viên
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable int id) {
        service.delete(id);
    }

    // 3. Tìm kiếm theo tên
    @GetMapping("/search")
    public List<Student> searchByName(@RequestParam String name) {
        return service.findByName(name);
    }

    // 4. Lấy sinh viên theo ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable int id) {
        return service.getStudentById(id);
    }

    // 5. Lấy danh sách sinh viên
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    // 6. Cập nhật sinh viên
    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable int id,
            @RequestBody Student student) {

        Student existing = service.getStudentById(id);
        if (existing == null) return null;

        existing.setName(student.getName());
        existing.setAge(student.getAge());
        existing.setEmail(student.getEmail());

        return service.save(existing);
    }
}
