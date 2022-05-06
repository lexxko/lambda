package org.my.config;

import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.Message;
import reactor.core.publisher.Sinks;

import java.lang.reflect.Field;

@Deprecated
public class FluxMessageChannelSafe extends FluxMessageChannel {

    @Override
    protected boolean doSend(Message<?> message, long timeout) {
        try {
            final Field sinkField = FluxMessageChannel.class.getDeclaredField("sink");
            sinkField.setAccessible(true);
            //noinspection unchecked
            final Sinks.Many<Message<?>> sink = (Sinks.Many<Message<?>>) sinkField.get(this);
            if(sink.currentSubscriberCount() <= 0) {
                logger.warn("The [" + this.getBeanName() + "] doesn't have subscribers to accept messages");
                return true;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return super.doSend(message, timeout);
    }
}
