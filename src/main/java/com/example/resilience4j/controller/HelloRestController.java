package com.example.resilience4j.controller;

import com.example.resilience4j.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloRestController {

    private final HelloService helloService;

    public HelloRestController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("hello");
        return helloService.greeting();
    }

    @GetMapping("/test1")
    public String test1() {
        log.info("test1");
        return helloService.test1();
    }

    @GetMapping("/test2")
    public String test2() {
        log.info("test2");
        return helloService.test2();
    }

    @GetMapping("/retry")
    public String retry() {
        log.info("retry");
        return helloService.retry();
    }

}

