package com.example.reactorspringmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SpringBootApplication
public class ReactorSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactorSpringMvcApplication.class, args);
    }

    @GetMapping("/mvc/{msg}")
    public Map<String, String> testWithWebflux(@PathVariable String msg) throws Exception {
        Thread currentThread = Thread.currentThread();

        System.out.println("thread-id : " + currentThread.getName());

        return Map.of("thread-id", currentThread.getName()
                , "msg", msg.toString()
                , "content", "Get WebFlux content is %s".formatted(msg));
    }

}
