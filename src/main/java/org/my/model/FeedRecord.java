package org.my.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Data
@AllArgsConstructor
@Container(containerName = "feed_records")
public class FeedRecord {
    @Id
    private Long id;
    @PartitionKey
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    private String title;
    private String link;

    public static long getId(@NonNull LocalDate date,
                             @NonNull String title,
                             @NonNull String link) {
        return getCRC32Checksum(Stream.of(date.toString(), title, link).toString().getBytes());
    }

    private static long getCRC32Checksum(byte[] bytes) {
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
