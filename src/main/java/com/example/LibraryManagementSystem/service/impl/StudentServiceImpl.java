package com.example.LibraryManagementSystem.service.impl;

import com.example.LibraryManagementSystem.dto.StudentDTO;
import com.example.LibraryManagementSystem.model.Student;
import com.example.LibraryManagementSystem.repository.BorrowHistoryRepository;
import com.example.LibraryManagementSystem.repository.StudentRepository;
import com.example.LibraryManagementSystem.service.StudentService;
import com.example.LibraryManagementSystem.util.LoggerUtil;
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
        LoggerUtil.info("Adding student: " + studentDTO);
        Student student = modelMapper.map(studentDTO, Student.class);
        Student saved = studentRepository.save(student);
        LoggerUtil.info("Student saved: " + saved);
        return modelMapper.map(saved, StudentDTO.class);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        LoggerUtil.info("Updating student with id: " + id + ", data: " + studentDTO);
        Student student;
        try {
            student = studentRepository.findById(id).orElseThrow(() -> new com.example.LibraryManagementSystem.exception.StudentNotFoundException("Student not found"));
        } catch (com.example.LibraryManagementSystem.exception.StudentNotFoundException e) {
            LoggerUtil.error("Student not found for update, id: " + id);
            throw e;
        }
        modelMapper.map(studentDTO, student);
        Student updated = studentRepository.save(student);
        LoggerUtil.info("Student updated: " + updated);
        return modelMapper.map(updated, StudentDTO.class);
    }

    @Override
    public void deleteStudent(Long id) {
        LoggerUtil.info("Deleting student with id: " + id);
        try {
            studentRepository.deleteById(id);
        } catch (Exception e) {
            LoggerUtil.error("Error deleting student with id: " + id + ", error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        LoggerUtil.info("Fetching all students");
        List<Student> students = studentRepository.findAll();
        LoggerUtil.debug("Total students fetched: " + students.size());
        return mapStudentsToDTOsWithBorrowCount(students);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        LoggerUtil.info("Fetching student with id: " + id);
        Student student;
        try {
            student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        } catch (RuntimeException e) {
            LoggerUtil.error("Student not found for id: " + id);
            throw e;
        }
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public List<StudentDTO> searchStudents(String name) {
        LoggerUtil.info("Searching students with keyword: " + name);
        List<Student> students = studentRepository.findByNameContaining(name);
        LoggerUtil.debug("Found " + students.size() + " students for keyword '" + name + "'");
        return mapStudentsToDTOsWithBorrowCount(students);
    }

    @Override
    public List<StudentDTO> findById(Long id) {
        LoggerUtil.info("Finding students by id: " + id);
        List<Student> students = studentRepository.findById(id)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        LoggerUtil.debug("Found " + students.size() + " students for id '" + id + "'");
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