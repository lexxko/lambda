package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.channels.Channel;

@Configuration
public class FeedCfg {

    @Value("${lambda.feedUrl}")
    private String feedUrl;

    @Bean("feedChannel")
    MessageChannel channel() {
        return new DirectChannel();
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
        return new FeedEntryMessageSource(urlResource, "myKey");
    }

//    @Bean
//    public IntegrationFlow feedFlow() {
//
//    }
}
