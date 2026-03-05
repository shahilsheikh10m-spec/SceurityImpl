package com.shahil.controller;

import com.shahil.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/url")
public class StudentController {

    private List<Student> studentList=new ArrayList<>(List.of(
            new Student(1, "Navin", 60),
            new Student(2,"shahil",70)
    ));
    @GetMapping("/students")
    public List<Student> getStudentList() {
        return studentList;
    }
    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");

    }
    @PostMapping("/student")
    public Student addStudent(@RequestBody Student student){
        studentList.add(student);
        return student;

    }
}
