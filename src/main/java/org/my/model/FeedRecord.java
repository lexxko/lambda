package org.my.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@Container(containerName = "feed_records")
public class FeedRecord {
    @Id
    private Long checksum;
    @PartitionKey
    private LocalDate date;
    private String body;
    private String link;
}
