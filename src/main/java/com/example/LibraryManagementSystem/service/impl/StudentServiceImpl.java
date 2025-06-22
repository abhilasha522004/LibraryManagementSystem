package com.example.LibraryManagementSystem.service.impl;

import com.example.LibraryManagementSystem.dto.StudentDTO;
import com.example.LibraryManagementSystem.model.Student;
import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import com.example.LibraryManagementSystem.repository.StudentRepository;
import com.example.LibraryManagementSystem.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StudentDTO addStudent(StudentDTO studentDTO) {
        Student student = modelMapper.map(studentDTO, Student.class);
        return modelMapper.map(studentRepository.save(student), StudentDTO.class);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.StudentNotFoundException("Student not found"));
        modelMapper.map(studentDTO, student);
        return modelMapper.map(studentRepository.save(student), StudentDTO.class);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return mapStudentsToDTOsWithBorrowCount(students);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        return modelMapper.map(studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found")), StudentDTO.class);
    }

    @Override
    public List<StudentDTO> searchStudents(String name) {
        List<Student> students = studentRepository.findByNameContaining(name);
        return mapStudentsToDTOsWithBorrowCount(students);
    }

    @Override
    public List<StudentDTO> findById(Long id) {
        List<Student> students = studentRepository.findById(id)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        return mapStudentsToDTOsWithBorrowCount(students);
    }

    private List<StudentDTO> mapStudentsToDTOsWithBorrowCount(List<Student> students) {
        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> borrowedCounts = borrowHistoryRepository.findByReturnDateIsNull().stream()
                .collect(Collectors.groupingBy(bh -> bh.getStudent().getId(), Collectors.counting()));

        return students.stream().map(student -> {
            StudentDTO dto = modelMapper.map(student, StudentDTO.class);
            dto.setBorrowedBooksCount(borrowedCounts.getOrDefault(student.getId(), 0L).intValue());
            return dto;
        }).collect(Collectors.toList());
    }
} 