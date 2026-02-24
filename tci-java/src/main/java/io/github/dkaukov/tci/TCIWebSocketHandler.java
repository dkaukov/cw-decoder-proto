package io.github.dkaukov.tci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TCIWebSocketHandler extends AbstractWebSocketHandler {

  @Autowired
  private TCITrxState tciTrxState;

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    log.info("TCI: established connection - " + session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.trace("TCI: {}", message.getPayload());
    parseMessage(message.getPayload());
  }

  @Override
  protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
    log.trace("TCI: {}", message.getPayload());
    TCIIQData payload = TCIIQData.deserialize(message.getPayload());
    applicationEventPublisher.publishEvent(payload);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    log.info("TCI: closed connection - " + session);
  }

  void parseMessage(String message) {
    String trimmed = message == null ? "" : message.trim();
    if (trimmed.isEmpty()) {
      return;
    }

    String content = trimmed.endsWith(";")
        ? trimmed.substring(0, trimmed.length() - 1)
        : trimmed;

    String[] payload = content.split(":", 2);
    String cmd = payload[0];
    String[] params = payload.length == 2 && !payload[1].isEmpty()
        ? payload[1].split(",")
        : new String[0];

    try {
      if (this.parseCommand(cmd, params)) {
        log.info("<<: \"{}\"", message);
      }
    } catch (RuntimeException ex) {
      log.warn("TCI parser failed for '{}': {}", message, ex.getMessage());
    }
  }

  boolean parseCommand(String cmd, String[] params) {
    return tciTrxState.parseCommand(cmd, params);
  }

}
