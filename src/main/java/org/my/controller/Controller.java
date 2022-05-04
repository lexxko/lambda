package org.my.controller;

import org.my.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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

    @GetMapping(path = "feedDisplay")
    public Flux<String> feedDisplay() {
        return feedService.feedDisplay()
                .map(Controller::createHyperlink);
    }

    private static String createHyperlink(Tuple2<String, String> tuple) {
        if (tuple.getT1().contains("No data") || tuple.getT2().contains("No data")) {
            return "No data";
        }

        return String.format("<a href=%s>%s</a><br>", tuple.getT1(), tuple.getT2());
    }
}
