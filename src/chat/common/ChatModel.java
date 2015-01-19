package chat.common;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class ChatModel {
	public final List<ChatRoom> roomList;
	public final List<UserData> userList;
	public final Multimap<UserData,ChatRoom> userRoomMap;
	
	public ChatModel(List<UserData> ul) {
		roomList = new ArrayList<ChatRoom>();
		if (ul != null)
			userList = ul;
		else userList = new ArrayList<UserData>();
		userRoomMap = ArrayListMultimap.create();
	}
}
