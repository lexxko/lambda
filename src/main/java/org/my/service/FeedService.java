package org.my.service;

import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.my.LambdaApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FeedService {
    public static final Logger log = LoggerFactory.getLogger(FeedService.class);

    private final Object monitor = new Object();
    private final FluxMessageChannel channel;
    private final FeedEntryMessageSource source;
    private final LambdaApplication application;
    private @Nullable Disposable disposable = null;
    private final List<Tuple2<String, String>> feed = new ArrayList<>();

    @Autowired
    public FeedService(FluxMessageChannel channel,
                       FeedEntryMessageSource source,
                       LambdaApplication application) {
        this.channel = channel;
        this.source = source;
        this.application = application;
    }

    public Mono<String> feedStart() {
        synchronized (this.monitor) {
            if (!isDisposed()) {
                final String message = "Feeding already started!";
                log.info(message);
                return Mono.just(message);
            }

            disposable = Flux.from(channel)
                    .doOnSubscribe(s -> log.info("Feeding started!"))
                    .map(Message::getPayload)
                    .filter(Objects::nonNull)
                    .map(msg -> (SyndEntryImpl) msg)
                    .filter(msg -> !msg.getTitle().isEmpty() && !msg.getLink().isEmpty())
                    .doOnNext(this::processMessage)
                    .doOnError(ClassCastException.class, e -> log.error("Class cast error: " + e.getMessage()))
                    .doOnError(err -> log.error(err.toString()))
                    .doOnComplete(() -> log.info("Feeding completed!"))
                    .subscribe();
        }
        return Mono.just("Feeding started!");
    }

    public Mono<String> feedStop() {
        synchronized (this.monitor) {
            if (isDisposed()) {
                final String message = "Feeding already stopped!";
                log.info(message);
                return Mono.just(message);
            }

            Objects.requireNonNull(disposable).dispose();
            log.info("Feeding stopped!");
        }
        return Mono.just("Feeding stopped!");
    }

    public Flux<Tuple2<String, String>> feedDisplay() {
        if (feed.isEmpty()) {
            log.info("No data to display");
            return Flux.just(Tuples.of("No data", "No data"));
        }
        log.info("Feed data displayed");
        return Flux.fromIterable(feed);
    }

    public Mono<String> feedClear() {
        feed.clear();
        log.info("Feed data cleared");

        return Mono.just("Cleared!");
    }

    public Mono<String> feedReset() {
        return Mono.just("Reset!")
                .doOnEach(x -> log.info("Reset!"))
                .doFinally(x -> LambdaApplication.restart());
    }

    private void processMessage(SyndEntryImpl msg) {
        final String link = removeUrlParams(msg.getLink());
        final String title = msg.getTitle();
        feed.add(Tuples.of(link, title));
        log.info(title + " " + link);
    }

    private boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

    private static String removeUrlParams(String url) {
        return url.substring(0, url.lastIndexOf('?'));
    }
}
