package com.test.practice;

import org.springframework.stereotype.Component;

@Component
public class Engine {

    public String start() {
        return "Engine started 🚗";
    }
}
