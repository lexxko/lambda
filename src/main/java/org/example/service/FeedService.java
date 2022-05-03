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

    public void startFeed() {
        if (disposable != null && !disposable.isDisposed()) {
            log.info("Feeding already started!");
            return;
        }

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
    }

    public void stopFeed() {
        if (disposable == null || disposable.isDisposed()) {
            log.info("Feeding already stopped!");
            return;
        }

        disposable.dispose();
        log.info("Feeding stopped!");
    }

    private static String removeUrlParams(String url) {
        return url.substring(0, url.lastIndexOf('?'));
    }
}
