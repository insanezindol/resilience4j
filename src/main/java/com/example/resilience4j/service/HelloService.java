package com.example.resilience4j.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class HelloService {

    @CircuitBreaker(name = "hello", fallbackMethod = "helloFallback")
    public String greeting() {
        log.info("service : greeting");
        randomException();
        return "hello world!";
    }

    private String helloFallback(Throwable t) {
        return "hello : fallback invoked! exception type : " + t.getClass();
    }

    @CircuitBreaker(name = "test1", fallbackMethod = "test1Fallback")
    public String test1() {
        randomException();
        return "hello test1!";
    }

    private String test1Fallback(Throwable t) {
        return "test1 : fallback invoked! exception type : " + t.getClass();
    }

    @CircuitBreaker(name = "test2", fallbackMethod = "test2Fallback")
    public String test2() {
        randomException();
        return "hello test2!";
    }

    private String test2Fallback(Throwable t) {
        return "test2 : fallback invoked! exception type : " + t.getClass();
    }

    private void randomException() {
        int randomInt = new Random().nextInt(10);
        log.info("randomInt : {}", randomInt);
        if(randomInt < 8) {
            throw new RuntimeException("failed");
        }
    }

    @Retry(name="retry", fallbackMethod = "helloRetryFallback")
    public String retry() {
        log.info("service : retry");
        randomException();
        return "hello retry world!";
    }

    private String helloRetryFallback(Throwable t) {
        return "hello : retry : fallback invoked! exception type : " + t.getClass();
    }

}
