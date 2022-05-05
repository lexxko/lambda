package org.my.config;

import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.core.io.Resource;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Queue;

public class FeedEntryMessageSourceWithReset extends FeedEntryMessageSource {
    public FeedEntryMessageSourceWithReset(URL feedUrl, String metadataKey) {
        super(feedUrl, metadataKey);
    }

    public FeedEntryMessageSourceWithReset(Resource feedResource, String metadataKey) {
        super(feedResource, metadataKey);
    }

    public void reset() throws NoSuchFieldException, IllegalAccessException {
        final Field lastTimeField = FeedEntryMessageSource.class.getDeclaredField("lastTime");
        lastTimeField.setAccessible(true);
        lastTimeField.set(this, -1); // initial value
        logger.info("Last time was reset");

        final Field entriesField = FeedEntryMessageSource.class.getDeclaredField("entries");
        entriesField.setAccessible(true);
        //noinspection unchecked
        final Queue<SyndEntry> entries = (Queue<SyndEntry>) entriesField.get(this);
        int num = 0;
        while (!entries.isEmpty()) {
            entries.poll();
            num++;
        }
        logger.info("Entries were dropped: " + num);
    }
}
