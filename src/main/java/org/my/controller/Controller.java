package org.my.controller;

import org.my.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Function;

@RestController
@RequestMapping("feed")
public class Controller {
    private final FeedService feedService;

    @Autowired
    public Controller(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping(path = "/")
    public Flux<String> home(@RequestHeader("Host") String host) {
        final BiFunction<String, Boolean, String> refFun = (path, ing) ->
                String.format("<a href=http://%s/feed/%s>/%s</a> - to %s feed%s<br>",
                        host, path, path, path, ing ? "ing" : "");

        return Flux.just(
                "<a href=http://" + host + "/feed/hi>/hi</a> (parameter \"name\" required) <br>",
                refFun.apply("start", true),
                refFun.apply("stop", true),
                refFun.apply("display", false),
                refFun.apply("clear", false)
        );
    }

    @GetMapping(path = "hi")
    public Mono<String> execute(@RequestParam("name") String name) {
        return Mono.just(String.format("Hi, %s!<br>By the way, my server time is %s", name, LocalTime.now()));
    }

    @GetMapping(path = "start")
    public Mono<String> feedStart() {
        return feedService.feedStart();
    }

    @GetMapping(path = "stop")
    public Mono<String> feedStop() {
        return feedService.feedStop();
    }

    @GetMapping(path = "display")
    public Flux<String> feedDisplay() {
        return feedService.feedDisplay()
                .map(Controller::createHyperlink);
    }

    @GetMapping(path = "clear")
    public Mono<String> feedClear() {
        return feedService.feedClear();
    }

    private static String createHyperlink(Tuple2<String, String> tuple) {
        if (tuple.getT1().contains("No data") || tuple.getT2().contains("No data")) {
            return "No data";
        }

        return String.format("<a href=%s>%s</a><br>", tuple.getT1(), tuple.getT2());
    }
}
