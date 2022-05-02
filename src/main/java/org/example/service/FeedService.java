package org.example.service;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

@Service
public class FeedService {
    public static final Logger log = LoggerFactory.getLogger(FeedService.class);
    private final FluxMessageChannel channel;

    @Autowired
    public FeedService(FluxMessageChannel channel) {
        this.channel = channel;
    }

    public void processFeed() {
        channel.subscribe(new Subscriber<Message<?>>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info(s.toString());
            }

            @Override
            public void onNext(Message<?> message) {
                log.info(message.getPayload().toString());
                System.out.println(message.getPayload());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void processMessage(Message message){

    }


}
