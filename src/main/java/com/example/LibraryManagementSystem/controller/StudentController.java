package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.dto.StudentDTO;
import com.example.LibraryManagementSystem.service.StudentService;
import com.example.LibraryManagementSystem.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping
    public StudentDTO addStudent(@RequestBody StudentDTO studentDTO) {
        LoggerUtil.info("Adding student: " + studentDTO);
        return studentService.addStudent(studentDTO);
    }

    @PutMapping("/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        LoggerUtil.info("Updating student with id: " + id + ", data: " + studentDTO);
        return studentService.updateStudent(id, studentDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        LoggerUtil.info("Deleting student with id: " + id);
        studentService.deleteStudent(id);
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        LoggerUtil.info("Fetching all students");
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        LoggerUtil.info("Fetching student with id: " + id);
        return studentService.getStudentById(id);
    }

    @GetMapping("/search")
    public List<StudentDTO> searchStudents(@RequestParam(required = false) String id,
                                           @RequestParam(required = false) String name) {
        LoggerUtil.info("Searching students with params - id: " + id + ", name: " + name);
        if (id != null && !id.isEmpty()) {
            return studentService.searchStudents(id);
        } else if (name != null && !name.isEmpty()) {
            return studentService.searchStudents(name);
        } else {
            return studentService.getAllStudents();
        }
    }
}
