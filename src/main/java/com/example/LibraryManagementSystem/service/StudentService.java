package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.dto.StudentDTO;
import java.util.List;

public interface StudentService {
    StudentDTO addStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Long id);
    List<StudentDTO> searchStudents(String name);
    List<StudentDTO> findById(Long id);
} 