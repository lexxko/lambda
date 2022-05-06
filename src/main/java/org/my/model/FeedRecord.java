package org.my.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
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
    @PartitionKey
    private Long id;
    private String date;
    private String title;
    private String link;

    public static FeedRecord create(@NonNull LocalDate date, @NonNull String title, @NonNull String link) {
        return new FeedRecord(createId(date, title, link), date.toString(), title, link);
    }

//    @Transient
//    public void setDate(LocalDate date) {
//        this.date = date.toString();
//    }
//
//    @Transient
//    public LocalDate getDate() {
//        return LocalDate.parse(date);
//    }

    public static long createId(@NonNull LocalDate date,
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
