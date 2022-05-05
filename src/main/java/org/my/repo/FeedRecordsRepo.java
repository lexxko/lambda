package org.my.repo;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.my.model.FeedRecord;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface FeedRecordsRepo extends CosmosRepository<FeedRecord, Long> {
    Flux<FeedRecord> findByDateBetween(LocalDate from, LocalDate to);
}