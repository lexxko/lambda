package org.my.repo;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import org.my.model.FeedRecord;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface FeedRecordsRepo extends ReactiveCosmosRepository<FeedRecord, String> {

    default Flux<FeedRecord> findAllByDateBetween(LocalDate from, LocalDate to) {
        return findAllByDateBetween(from.toString(), to.toString()); // TODO Implicit equality LocalDate.toString and @JsonFormat(pattern = "yyyy-MM-dd") in FeedRecord class
    }

    Flux<FeedRecord> findAllByDateBetween(String from, String to);
}