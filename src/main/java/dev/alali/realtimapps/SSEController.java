package dev.alali.realtimapps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Random;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class SSEController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSEController.class);

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public SSEController(RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @GetMapping(value = "/sse", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse() {
        String channel = authenticate();
        LOGGER.info("Got a connection to channel {}", channel);
        return Flux.create(sink -> {
            RedisMessageListener listener = new RedisMessageListener(sink);
            redisMessageListenerContainer.addMessageListener(listener, new ChannelTopic(channel));

            Disposable disposable = () -> redisMessageListenerContainer.removeMessageListener(listener);

            sink.onDispose(disposable);
        });
    }

    private String authenticate() {
        return "abc" + new Random().nextInt(1000);
    }


}
