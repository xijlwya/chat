package chat.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
    public final List<String> contentList;
    public final MessageType type;
    
    public enum MessageType {
        LOGIN, LOGOUT, CHAT, REGISTER, REJECT, UPDATE, ANNOUNCE, JOIN, PRIVATE
    }
    
    //login / register message constructor
    public ChatMessage(MessageType type, List<String> content) {
        this.type = type;
        contentList = content;
    }
    
    public ChatMessage(MessageType type, String content) {
        this.type = type;
        contentList = new ArrayList<String>();
        contentList.add(content);
    }
    
    //administrative message constructor (logout)
    public ChatMessage(MessageType type) {
        this.type = type;
        contentList = null;
    }
    
    //simplified chat message constructor 
    public ChatMessage(String content) {
        this.type = MessageType.CHAT;
        contentList = new ArrayList<String>();
        contentList.add(content);
    }
    
    public String toString() {
        return contentList.get(0);
    }
}
