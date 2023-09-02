package dev.alali.realtimapps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import reactor.core.publisher.FluxSink;


public class RedisMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageListener.class);

    private final FluxSink<String> sink;

    public RedisMessageListener(FluxSink<String> sink) {
        this.sink = sink;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        LOGGER.info("Received message: {} on channel: {}", messageBody, new String(message.getChannel()));
        sink.next(messageBody);
    }

}
