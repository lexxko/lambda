package org.my.repo;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import org.my.model.FeedRecord;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface FeedRecordsRepo extends ReactiveCosmosRepository<FeedRecord, Long> {
    Flux<FeedRecord> findAllByDateBetween(LocalDate from, LocalDate to);
}