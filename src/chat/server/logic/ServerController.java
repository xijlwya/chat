package chat.server.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import chat.common.ChatMessage;
import chat.common.ChatModel;
import chat.common.ChatRoom;
import chat.common.LogFileWriter;
import chat.common.SerializableUserList;
import chat.common.UserData;
import chat.common.ChatMessage.MessageType;
import chat.server.swing.ServerGUI;
import chat.server.swing.ServerGUIListener;

public class ServerController {
	private ChatServer server;
	private ServerGUI gui;
	private ChatModel model;
	
	public final ChatRoom defaultRoom;
	private List<UserData> banList;
	private final String userListFileName = "./data.dat";
	private final String logFileName = "./log.txt";
	private SerializableUserList userSerialList;
	private LogFileWriter log;
	private final String warnMessage = "You've been warned!";
	private final String banMessage = "You've been banned!";
	private final String kickMessage = "You've been kicked!";
	
	public ServerController() {
		userSerialList = new SerializableUserList(userListFileName);
		try {
			userSerialList.readUserListFromFile();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		model = new ChatModel(userSerialList.getUserList());
		
		log = new LogFileWriter(logFileName);
		
		gui = new ServerGUI(this, model);
		new ServerGUIListener(this, gui);
        
        defaultRoom = new ChatRoom("Lobby");
		makeRoom(defaultRoom);
		banList = new ArrayList<UserData>();
        
        server = new ChatServer(this, 1234);
        server.initialiseThreadMap(model.userList);
        server.listen();
	}
	
	public void post(ChatMessage msg, ChatRoom room) {
		//post a msg in a room
		for (Map.Entry<UserData, ChatRoom> entry : model.userRoomMap.entries()) {
			if (entry.getValue().equals(room)) {
				try {
					server.sendToUser(msg, entry.getKey());
				} catch (IOException e) {
					print("Unable to send message \"" + msg + "\" to user [" + entry.getKey().name + "]");
				}
			}
		}
	}
	
	public void processMessage(ChatMessage msg, UserData user) {
		//post a message from a user in the room he is in
		ChatRoom room = null;
		for(ChatRoom r : model.userRoomMap.get(user)) {
			//find the one non-private room
			if (!r.isPrivate)
				room = r;
		}
		print("["+user.name + "] in room ["+room.name+"]: " + msg.toString());
		post(new ChatMessage("["+user.name + "]: " + msg.toString()), room);
	}
	
	public void whisper(ChatMessage msg, UserData user) {
		try {
			server.sendToUser(msg, user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void demandPrivateChat(UserData init, UserData target) {
		//user1 demands to open a room with user2
		if (model.userRoomMap.containsKey(init) && model.userRoomMap.containsKey(target)) {
			whisper(new ChatMessage(MessageType.PRIVATE, "User ["+init.name+"] wants to engange in a private chat with you."),target);
			model.userRoomMap.put(init, new ChatRoom("Private ["+init.name+"]>["+target.name+"]",true));
			//whisper(new ChatMessage(MessageType.PRIVATE_INIT), user1);
		}
	}
	
	public void acceptPrivateChat(UserData init, UserData target) {
		if (model.privateChatMap.containsKey(init) && model.userRoomMap.containsKey(init) && model.userRoomMap.containsKey(target)) {
			model.privateChatMap.put(target, init);
			makeRoom(new ChatRoom("Private room: ["+init.name+"] <-> ["+target.name+"]",true));
			
		}
	}
	
	public void renameRoom(ChatRoom room, String name) {
		print("Changed room [" + room.name + "] into [" + name + "].");
		room.name = name;
		gui.repaint();
		sendRoomList();
	}
	
	public void makeRoom(ChatRoom room) {
		if (!model.roomList.contains(room)) {
			model.roomList.add(room);
			gui.repaint();
			print("Room [" + room + "] created.");
		}
	}
	
	public void deleteRoom(ChatRoom room) {
		if (model.roomList.contains(room)) {
			print("Deleting room [" + room.name + "]...");
			for (Map.Entry<UserData, ChatRoom> entry : model.userRoomMap.entries()) {
				if (entry.getValue().equals(room)) {
					join(entry.getKey(), defaultRoom);
					whisper(new ChatMessage(MessageType.ANNOUNCE, "Your room has been deleted."), entry.getKey());
					print("Moved [" + entry.getKey() + "] to [" + defaultRoom.name + "].");
				}
			}
			sendUserList(defaultRoom);
			model.roomList.remove(room);
			gui.repaint();
			sendRoomList();
			print("Room [" + room.name + "] deleted.");
		}
	}
	
	public void join(UserData user, ChatRoom room) {
		if (model.userList.contains(user)) {
			if (model.userRoomMap.containsKey(user)) {
				//user has been in another non-private room before
				ChatRoom nonPrivateRoom = null;
				for(ChatRoom r : model.userRoomMap.get(user)) {
					//find the one non-private room
					if (!r.isPrivate)
						nonPrivateRoom = r;
				}
				
				post(new ChatMessage("["+user.name+"] switched to room ["+room.name+"]."),nonPrivateRoom);
				print("["+user.name+"] left ["+model.userRoomMap.get(user)+"].");
				sendUserList(nonPrivateRoom);
				sendRoomList();
			} else if (model.roomList.contains(room)) {
				//the destination room already exists
				model.userRoomMap.put(user, room);
				print("[" + user.name + "] joined room [" + room.name + "].");
				sendUserList(room);
				sendRoomList();
				post(new ChatMessage("[" + user.name + "] joined"), room);
			} else {
				//create a new room for the user
				makeRoom(room);
				model.userRoomMap.put(user, room);
				sendUserList(room);
				sendRoomList();
			}
		} else {
			//user not found
			System.err.println("Invalid call to ServerController.join with parameters "+user.name+"//"+room.name+".");
		}
	}

	public void join(UserData client, String string) {
		for (ChatRoom room : model.roomList) {
			if (room.name.equals(string)) {
				join(client, room);
			}
		}
	}

	public ChatMessage login(UserData user) {
		//is called by ChatServer class
		
		//TODO send login related message to user, see also ChatServer.login()
		//Problem: message can only be sent after the completion of this method (see ChatServer.login)
		
		for (UserData scan : model.userList) {
    		if (scan.equals(user)) { //user already exists
    			if (getBanList().contains(user)) {
    				print("Rejected login from banned user ["+user.name+"].");
    				return new ChatMessage(MessageType.REJECT, "You're banned.");
    			} else if (model.userRoomMap.containsKey(scan)) {
    				print("Multilog attempt from user ["+user.name+"] detected.");
    				return new ChatMessage(MessageType.REJECT, "Already logged in.");
    			} else {
    				print("User ["+user.name+"] logged in.");
	        		return new ChatMessage(MessageType.LOGIN, "Login successful!");
    			}
    		} else if (scan.name.equals(user.name)) { //user name exists, but passwords do not match
    			print("Faulty login attempt for ["+user.name+"].");
    			return new ChatMessage(MessageType.REJECT, "Bad password.");
    		} 
    	}
		//user does not exist
		register(user);
		print("User [" + user.name + "] has been registered.");

		return new ChatMessage(MessageType.LOGIN, "Registration successful!");
	}

	private void register(UserData user) {
		model.userList.add(user);
		userSerialList.setUserList(model.userList);
		userSerialList.writeUserListToFile();
	}
	
	public void logout(UserData user) {
		//is called by ChatServer
		if (model.userRoomMap.containsKey(user)) {
			ChatRoom nonPrivateRoom = null;
			for(ChatRoom r : model.userRoomMap.get(user)) {
				//find the one non-private room
				if (!r.isPrivate)
					nonPrivateRoom = r;
			}
			ChatRoom leftRoom = nonPrivateRoom;
			Collection<ChatRoom> roomList = model.userRoomMap.get(user);
			model.userRoomMap.removeAll(user);
			sendUserList(leftRoom);
			for(ChatRoom room : roomList) {
				post(new ChatMessage(MessageType.ANNOUNCE, "["+user.name+"] logged out."), room);
			}
			print("[" + user.name + "] has been logged out.");
		}
	}

	public void kickUser(String userName) throws IOException {
		UserData selectedUser = findUserName(userName);
		if (model.userList.contains(selectedUser)) {
			whisper(new ChatMessage(MessageType.ANNOUNCE, kickMessage), selectedUser);
			print("[" + selectedUser.name + "] has been kicked.");
			logout(selectedUser);
			server.kickUser(selectedUser);
		}
	}

	public void banUser(String userName) throws IOException {
		UserData selectedUser = findUserName(userName);
		whisper(new ChatMessage(MessageType.ANNOUNCE, banMessage ), selectedUser);
		kickUser(userName);
		if (model.userList.contains(selectedUser)) {
			print("[" + selectedUser.name + "] has been banned.");
			getBanList().add(selectedUser);
		}
	}
	
	public void warnUser(String userName) {
		UserData selectedUser = findUserName(userName);
		whisper(new ChatMessage(MessageType.ANNOUNCE, warnMessage ), selectedUser);
		print("["+ selectedUser.name + "] has been warned.");
	}
	
	public void print(String msg) {
		gui.print(msg);
		log.logLine(msg);
	}

	public void sendRoomList() {
		//send all non-private rooms on the server
		List<String> sendRoomList = new ArrayList<String>();
		sendRoomList.add("roomlist");
		for (ChatRoom room : model.roomList) {
			if (!room.isPrivate)
				sendRoomList.add(room.name);
		}
		try {
			server.sendToAll(new ChatMessage(MessageType.UPDATE, sendRoomList));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendUserList(ChatRoom room) {		
		//send all users that are in the same room
		List<String> sendUserList = new ArrayList<String>();
		sendUserList.add("userlist");
		for (Map.Entry<UserData, ChatRoom> entry : model.userRoomMap.entries()) {
			if(entry.getValue().equals(room))
				sendUserList.add(entry.getKey().name);
		}
		post(new ChatMessage(MessageType.UPDATE, sendUserList), room);
	}
	
	private UserData findUserName(String name) {
		for (UserData user : model.userList)
			if (user.name.equals(name)) return user;
		return null;
	}

	/**
	 * @return the banList
	 */
	public List<UserData> getBanList() {
		return banList;
	}
	
	public List<UserData> getRegisteredUserList() {
		return userSerialList.getUserList();
	}
}
