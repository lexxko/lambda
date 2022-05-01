package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
public class Controller {

    @GetMapping(path = "hi")
    public Mono<String> execute(@RequestParam(name = "name") String name) {
        return Mono.just("Hi, " + name + "!");
    }
}
