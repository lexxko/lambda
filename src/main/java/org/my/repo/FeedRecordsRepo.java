package org.my.repo;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.my.model.FeedRecord;
import org.my.model.FeedRecordC;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface FeedRecordsRepo extends ReactiveCosmosRepository<FeedRecordC, Long> {

    default Flux<FeedRecordC> findAllByDateBetween(LocalDate from, LocalDate to) {
        return findAllByDateBetween(from.toString(), to.toString());
    }

    Flux<FeedRecordC> findAllByDateBetween(String from, String to);
}