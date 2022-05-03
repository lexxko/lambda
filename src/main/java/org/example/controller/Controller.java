package org.example.controller;

import org.example.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
public class Controller {
    private final FeedService feedService;
    @Autowired
    public Controller(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping(path = "hi")
    public Mono<String> execute(@RequestParam(name = "name") String name) {
        return Mono.just("Hi, " + name + "!");
    }

    @GetMapping(path = "startFeed")
    public void startFeed() {
        feedService.startFeed();
    }

    @GetMapping(path = "stopFeed")
    public void stopFeed() {
        feedService.stopFeed();
    }
}
