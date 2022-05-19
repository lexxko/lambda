package org.my.service;

import com.rometools.rome.feed.synd.SyndEntry;
import org.my.LambdaApplication;
import org.my.model.FeedRecord;
import org.my.repo.FeedRecordsRepo;
import org.my.util.ParseUtil;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FeedService {
    public static final Logger log = LoggerFactory.getLogger(FeedService.class);
    public static final String ZONE_ZERO_OFFSET = "+0";

    private final Object monitor = new Object();
    private final FluxMessageChannel channel;
    private final FeedRecordsRepo feedRecordsRepo;
    private @Nullable Disposable disposable = null;
    private final List<Tuple2<String, String>> feed = new ArrayList<>();

    @Autowired
    public FeedService(FluxMessageChannel channel, FeedRecordsRepo feedRecordsRepo) {
        this.channel = channel;
        this.feedRecordsRepo = feedRecordsRepo;
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
                    .map(msg -> (SyndEntry) msg)
                    .filter(msg -> !msg.getTitle().isEmpty() && !msg.getLink().isEmpty())
                    .doOnError(ClassCastException.class, e -> log.error("Class cast error: " + e.getMessage()))
                    .doOnError(err -> log.error(err.toString()))
                    .doOnComplete(() -> log.info("Feeding completed!"))
                    .flatMap(this::saveToDb)
                    .doOnNext(this::process)
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
        } else {
            log.info("Feed data displayed");
        }
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

    public Flux<Tuple2<String, String>> getArchivedFeed(@Nullable final String sFrom, @Nullable final String sTo) {
        final LocalDate from;
        LocalDate to;

        if (sFrom == null || sFrom.isEmpty()) {
            from = LocalDate.now();
            log.info("Select range lower bound set to current date");
        } else {
            from = ParseUtil.parseDate(sFrom, LocalDate::now);
        }
        if (sTo == null || sTo.isEmpty()) {
            to = LocalDate.now();
            log.info("Select range upper bound set to current date");
        } else {
            to = ParseUtil.parseDate(sTo, LocalDate::now);
        }

        if (to.isBefore(from)) {
            to = from;
            log.info("Upper bound date can't be before lower bound date. Set as equal");
        }

        return feedRecordsRepo.findAllByDateBetween(from, to)
                .doOnNext(rec -> log.debug("Archived record: {} {} {}", rec.getDate(), rec.getTitle(), rec.getLink()))
                .map(rec -> Tuples.of(rec.getLink(), rec.getTitle()));
    }

    private Mono<FeedRecord> saveToDb(SyndEntry msg) {
        final String link = removeUrlParams(msg.getLink());
        final String title = msg.getTitle();
        final LocalDate date = msg.getPublishedDate().toInstant().atZone(ZoneId.of(ZONE_ZERO_OFFSET)).toLocalDate();
        return feedRecordsRepo.save(new FeedRecord(date, title, link))
                .doOnSuccess(rec -> log.debug("Record saved to DB: {}", rec));
    }

    private void process(FeedRecord record) {
        final String link = record.getLink();
        final String title = record.getTitle();
        feed.add(Tuples.of(link, title));
        log.debug(title + " " + link);
    }

    private boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

    private static String removeUrlParams(String url) {
        return url.substring(0, url.lastIndexOf('?'));
    }
}
