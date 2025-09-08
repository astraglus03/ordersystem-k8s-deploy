package spring.ordersystem.common.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import spring.ordersystem.common.service.SseEmitterRegistry;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping("/connect")
    public SseEmitter subscribe(@RequestHeader("X-User-Email") String email) {
        SseEmitter sseEmitter = new SseEmitter(14400 * 60 * 1000L); // 10일정도 emitter유효기간 설정
        sseEmitterRegistry.addSseEmitter(email, sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("연결되었습니다. 알림을 받으실 수 있습니다."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sseEmitter;
    }

    @GetMapping("/disconnect")
    public void unsubscribe(@RequestHeader("X-User-Email") String email) {
        sseEmitterRegistry.removeSseEmitter(email);
    }
}
