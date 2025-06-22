package com.example.LibraryManagementSystem.web;

import com.example.LibraryManagementSystem.dto.StudentDTO;
import com.example.LibraryManagementSystem.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students/v1")
public class StudentWebController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/view")
    public String showStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "student-list";
    }

    @GetMapping("/add")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "student-edit";
    }

    @PostMapping("/add")
    public String addStudent(StudentDTO studentDTO, Model model) {
        try {
            studentService.addStudent(studentDTO);
            return "redirect:/students/v1/view";
        } catch (Exception ex) {
            model.addAttribute("student", studentDTO);
            model.addAttribute("errorMessage", ex.getMessage());
            return "student-edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students/v1/view";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student-edit";
    }

    @PostMapping("/update")
    public String updateStudent(StudentDTO studentDTO, Model model) {
        try {
            studentService.updateStudent(studentDTO.getId(), studentDTO);
            return "redirect:/students/v1/view";
        } catch (Exception ex) {
            model.addAttribute("student", studentDTO);
            model.addAttribute("errorMessage", ex.getMessage());
            return "student-edit";
        }
    }

    @GetMapping("/search")
    public String searchStudents(@RequestParam(required = false) String id,
                                 @RequestParam(required = false) String name,
                                 Model model) {
        if (id != null && !id.isEmpty()) {
            model.addAttribute("students", studentService.searchStudents(id));
        } else if (name != null && !name.isEmpty()) {
            model.addAttribute("students", studentService.searchStudents(name));
        } else {
            model.addAttribute("students", studentService.getAllStudents());
        }
        return "student-list";
    }
} 