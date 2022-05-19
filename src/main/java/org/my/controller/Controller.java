package org.my.controller;

import org.my.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;

import java.time.LocalTime;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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
    public ResponseEntity<Mono<String>> execute(@RequestParam("name") String name) {
        return succeedRs(() -> Mono.just(
                String.format("Hi, %s!<br>By the way, my server time is %s", name, LocalTime.now())), false);
    }

    @GetMapping(path = "start")
    public ResponseEntity<Mono<String>> feedStart() {
        return succeedRs(feedService::feedStart, false);
    }

    @GetMapping(path = "stop")
    public ResponseEntity<Mono<String>> feedStop() {
        return succeedRs(feedService::feedStop, false);
    }

    @GetMapping(path = "display")
    public ResponseEntity<Flux<Tuple2<String, String>>> feedDisplay() {
        return succeedRs(feedService::feedDisplay, true);
    }

    @GetMapping(path = "clear")
    public ResponseEntity<Mono<String>> feedClear() {
        return succeedRs(feedService::feedClear, false);
    }

    @GetMapping(path = "reset")
    public ResponseEntity<Mono<String>> feedReset() {
        return succeedRs(feedService::feedReset, false);
    }

    @GetMapping(path = "archive")
    public ResponseEntity<Flux<Tuple2<String, String>>> getArchivedFeed(@RequestParam(name = "from", required = false) @Nullable String from,
                                                                        @RequestParam(name = "to", required = false) @Nullable String to) {
        return succeedRs(() -> feedService.getArchivedFeed(from, to), true);
    }

    private static <T> ResponseEntity<T> succeedRs(Supplier<T> tSupplier, boolean json) {
        return ResponseEntity
                .ok()
                .header("Access-Control-Allow-Origin", "*") // TODO: remove (not critical because of Azure CORS overlay)
                .header("Content-Type", json ? "application/json" : "text/plain")
                .body(tSupplier.get());
    }
}
