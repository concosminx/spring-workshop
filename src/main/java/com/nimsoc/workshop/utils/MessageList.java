package com.nimsoc.workshop.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
public class MessageList {

  private final List<Message> messages = new ArrayList<>();

  public final void transferMessages(MessageList anotherMessageList) {
    anotherMessageList.getMessages().forEach(
        x -> {
          this.getMessages().add(x);
        }
    );
  }

  public final void addErrorMessage(String m) {
    this.messages.add(new Message(Severity.ERROR, m));
  }

  public final void addInfoMessage(String m) {
    this.messages.add(new Message(Severity.INFO, m));
  }

  public boolean hasMessages() {
    return messages.size() > 0;
  }

  public boolean hasErrorMessages() {
    return messages.stream().anyMatch(m -> m.getSeverity() == Severity.ERROR);
  }

  public enum Severity {
    INFO,
    ERROR
  }

  @RequiredArgsConstructor
  @Getter
  @Setter
  public static class Message {
    private final Severity severity;
    private final String message;
  }
}
