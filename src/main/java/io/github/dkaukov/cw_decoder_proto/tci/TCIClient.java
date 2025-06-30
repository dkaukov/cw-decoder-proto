package io.github.dkaukov.cw_decoder_proto.tci;

import java.io.IOException;
import java.net.URI;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TCIClient extends StandardWebSocketClient {

  @Autowired
  private TCIWebSocketHandler tciWebSocketHandler;
  private WebSocketSession webSocketSession = null;
  @Value("${tci.address:ws://localhost:50001}")
  private String tciAddress;

  public void connect() {
    this.execute(tciWebSocketHandler, new WebSocketHttpHeaders(), URI.create(tciAddress))
      .whenComplete((session, throwable) -> {
        if (throwable != null) {
          log.warn("TCI: {}", throwable.getMessage());
          return;
        }
        this.webSocketSession = session;
        try {
          session.setBinaryMessageSizeLimit(32768);
          session.sendMessage(new TextMessage("iq_samplerate:384000;"));
          // session.sendMessage(new TextMessage("iq_samplerate:192000;"));
          session.sendMessage(new TextMessage("iq_start:0;"));
        } catch (IOException e) {
          throw new RuntimeException("Failed to send initial TCI commands", e);
        }
      });
  }

  @Scheduled(fixedRate = 5000, initialDelay = 5000)
  public void tryConnect() {
    if ((webSocketSession == null) || (!webSocketSession.isOpen())) {
      connect();
    }
  }
}