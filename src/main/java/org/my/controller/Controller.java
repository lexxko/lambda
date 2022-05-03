package org.my.controller;

import org.my.service.FeedService;
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

    @GetMapping(path = "feedStart")
    public Mono<String> feedStart() {
        return feedService.feedStart();
    }

    @GetMapping(path = "feedStop")
    public Mono<String> feedStop() {
        return feedService.feedStop();
    }
}
