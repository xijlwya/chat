package chat.common;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	public List<ChatMessage> msgHistory;
	
	public String name;
	
	public final boolean isPrivate;
	
	public ChatRoom(String name) {
		this.name = name;
		isPrivate = false;
		msgHistory = new ArrayList<ChatMessage>();
	}
	
	public ChatRoom(String name, boolean p) {
		this.name = name;
		isPrivate = p;
		msgHistory = new ArrayList<ChatMessage>();
	}
	
	public void postMsg(ChatMessage msg) {
		msgHistory.add(msg);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object compare) {
		if (this.name.equals(((ChatRoom)compare).name))
			return true;
		else return false;
	}
}
