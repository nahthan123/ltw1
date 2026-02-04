package com.example.schoolmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.schoolmanager.model.Student;
import com.example.schoolmanager.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentWebController {

    @Autowired
    private StudentService service;

    // 1. Danh sách + tìm kiếm (ID hoặc TÊN)
    @GetMapping
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {

            // nếu nhập số → tìm theo ID
            if (keyword.matches("\\d+")) {
                int id = Integer.parseInt(keyword);
                Student student = service.getStudentById(id);

                if (student != null) {
                    model.addAttribute("students", List.of(student));
                } else {
                    model.addAttribute("students", List.of());
                }

            } else {
                // nếu nhập chữ → tìm theo tên
                model.addAttribute("students", service.findByName(keyword));
            }

        } else {
            model.addAttribute("students", service.getAllStudents());
        }

        model.addAttribute("keyword", keyword);
        return "students";
    }

    // 2. Form thêm
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    // 3. Lưu (thêm + sửa)
    @PostMapping("/save")
    public String save(@ModelAttribute Student student) {
        service.save(student);
        return "redirect:/students";
    }

    // 4. Form sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("student", service.getStudentById(id));
        return "student-form";
    }

    // 5. Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "redirect:/students";
    }
}
