package org.example.service;

import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.Objects;

@Service
public class FeedService {
    public static final Logger log = LoggerFactory.getLogger(FeedService.class);
    private final FluxMessageChannel channel;
    private @Nullable Disposable disposable = null;

    @Autowired
    public FeedService(FluxMessageChannel channel) {
        this.channel = channel;
    }

    public Mono<String> feedStart() {
        if (!isDisposed()) return Mono.just("Feeding already started!");

        disposable = Flux.from(channel)
                .doOnSubscribe(s -> log.info("Feeding started!"))
                .map(Message::getPayload)
                .filter(Objects::nonNull)
                .map(msg -> (SyndEntryImpl) msg)
                .filter(msg -> !msg.getTitle().isEmpty() && !msg.getLink().isEmpty())
                .doOnNext(msg -> log.info(msg.getTitle() + " " + removeUrlParams(msg.getLink())))
                .doOnError(ClassCastException.class, e -> log.error("Class cast error: " + e.getMessage()))
                .doOnError(err -> log.error(err.toString()))
                .doOnComplete(() -> log.info("Feeding completed!"))
                .subscribe();

        return Mono.just("Feeding started!");
    }

    public Mono<String> feedStop() {
        if (isDisposed()) return Mono.just("Feeding already stopped!");

        Objects.requireNonNull(disposable).dispose();
        log.info("Feeding stopped!");

        return Mono.just("Feeding stopped!");
    }

    private boolean isDisposed() {
        synchronized (this) {
            if (disposable == null || disposable.isDisposed()) {
                log.info("Feeding already stopped!");
                return true;
            }
            log.info("Feeding already started!");
            return false;
        }
    }

    private static String removeUrlParams(String url) {
        return url.substring(0, url.lastIndexOf('?'));
    }
}
