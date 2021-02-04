package lk.mobilevisions.kiki.chat.messages;

public interface ChatMessage {

  String getMessageBody();

  String getAuthor();

  String getDateCreated();
}
