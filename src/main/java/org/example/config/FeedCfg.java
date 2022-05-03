package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

@Configuration
public class FeedCfg {
    public static final Logger log = LoggerFactory.getLogger(FeedCfg.class);

    @Value("${lambda.feedUrl}")
    private String feedUrl;

    @Bean("feedChannel")
    FluxMessageChannel channel() {
        return new FluxMessageChannel();
    }

    @Bean
    @InboundChannelAdapter("feedChannel")
    FeedEntryMessageSource feedEntrySource() {
        UrlResource urlResource;

        try {
            urlResource = new UrlResource(feedUrl) {
                @Override
                protected void customizeConnection(HttpURLConnection connection) throws IOException {
                    super.customizeConnection(connection);
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(5000);
                }
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new FeedEntryMessageSource(urlResource, "lambda");
    }
}
