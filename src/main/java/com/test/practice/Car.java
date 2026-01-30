package com.test.practice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Car {
    @Autowired
    Engine engine;

    @GetMapping("/")
    public String home() {
        return "Spring Boot is running ✅";
    }

    @GetMapping("/car/drive")
    public String drive() {
        return "Car is driving 🚗";
    }
}
