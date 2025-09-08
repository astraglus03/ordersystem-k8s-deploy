package spring.ordersystem.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import spring.ordersystem.common.dto.SseMessageDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SseAlarmService implements MessageListener {

    //    SseEmitter는 연결된 사용자정보를 의미
    private final SseEmitterRegistry sseEmitterRegistry;
    private final RedisTemplate<String, String> redisTemplate;

    public SseAlarmService(SseEmitterRegistry sseEmitterRegistry, @Qualifier("ssePubSub") RedisTemplate<String, String> redisTemplate) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.redisTemplate = redisTemplate;
    }

//    특정 사용자에게 message발송

    public void publishMessage(String receiver, String sender, Long orderingId) {
        SseMessageDto dto = SseMessageDto.builder()
                .receiver(receiver)
                .sender(sender)
                .orderingId(orderingId)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String data = null;
        try {
            data = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage()) {
            };
        }

//        emitter객체를 통해 메시지 전송
        SseEmitter emitter = sseEmitterRegistry.getEmitter(receiver);

//        emitter객체가 현재 서버에 있으면, 직접 알림 발송, 그렇지 않으면, Redis에 publish
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("ordered").data(data));
                //    사용자가 로그아웃(새로고침)후에 다시 화면에 들어왔을때 알림메시지가 남아있으려면 DB에 추가적으로 저장 필요
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
//            채널은 가상의 공간이며 물리적으로 확인 불가.
            redisTemplate.convertAndSend("order-channel", data);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
//        message: 실질적인 메시지가 담겨있는 객체
//        pattern: 채널명
        ObjectMapper objectMapper = new ObjectMapper();
//        여러개의 채널을 구독하고 있을경우, 채널명으로 분기처리
        try {
            SseMessageDto dto = objectMapper.readValue(message.getBody(), SseMessageDto.class);
            SseEmitter emitter = sseEmitterRegistry.getEmitter(dto.getReceiver());
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("ordered").data(message.getBody()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //    만약, 해당 사용자가 현재 연결되어 있지 않다면, Redis에 publish
                redisTemplate.convertAndSend("order-channel", message.getBody());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SseMessageDto dto = objectMapper.readValue(message.getBody(), SseMessageDto.class);
            System.out.println("SSE 메시지 수신: " + dto);
            System.out.println("수신된 채널: " + new String(pattern));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
