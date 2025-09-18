package com.example.tag_dev.Config;


import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromQuery(session);
        if (token == null || jwtTokenProvider.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid JWT"));
            return;
        }
        Long userId = jwtTokenProvider.extractUserId(token);
        session.getAttributes().put("userId", userId);
        System.out.println("웹소켓 연결됨: " + userId);
    }

    private String getTokenFromQuery(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1];
        }
        return null;
    }
}
