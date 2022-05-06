package org.my.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.my.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
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
                refFun.apply("clear", false),
                refFun.apply("reset", false),
                refFun.apply("archive", false)
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

    @GetMapping(path = "reset")
    public Mono<String> feedReset() {
        return feedService.feedReset();
    }

    @GetMapping(path = "archive")
    public Flux<String> getArchivedFeed(@RequestParam(name = "from", required = false) @Nullable LocalDate from,
                                        @RequestParam(name = "to", required = false) @Nullable LocalDate to) {
        return feedService.getArchivedFeed(from, to)
                .map(Controller::createHyperlink);
    }

    private static String createHyperlink(Tuple2<String, String> tuple) {
        if (tuple.getT1().contains("No data") || tuple.getT2().contains("No data")) {
            return "No data";
        }

        return String.format("<a href=%s>%s</a><br>", tuple.getT1(), tuple.getT2());
    }
}
