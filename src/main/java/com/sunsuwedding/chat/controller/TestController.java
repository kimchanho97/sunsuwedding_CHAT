package com.sunsuwedding.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/api/chat/test")
    public String test() {
        return "hello world!";
    }
}