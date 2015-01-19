package chat.server.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.common.ChatMessage;
import chat.common.ChatMessage.MessageType;
import chat.common.UserData;



public class ChatServer {
    ServerController controller;
	private ServerSocket listener;
	private Map<UserData, ChatServerThread> threadMap;
    
    public ChatServer(ServerController sc, int port) {
        controller = sc;
        threadMap = new HashMap<UserData, ChatServerThread>();
        
    	try {
            listener = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void listen() {
        boolean listening = true;
        
        while(listening) {
            try {            
//                Socket newChatClient = listener.accept();
//                ChatServerThread serverThread = new ChatServerThread(newChatClient, this);
//                serverThread.start();
                
                new ChatServerThread(listener.accept(),this).start();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println("Server is not listening!");
                listening = false;
            }
        }
    }
    
    public void initialiseThreadMap(List<UserData> userList) {
    	//to be called by the controller
    	for (UserData u : userList) {
    		threadMap.put(u, null);
    	}
    }
    
    public void sendToUser(ChatMessage msg, UserData user) throws IOException {
    	if (threadMap.containsKey(user)){
    		threadMap.get(user).send(msg);
    	} else {
    		throw new IOException("ThreadMap does not contain user.");
    	}
    }
    
    public void sendToAll(ChatMessage msg) throws IOException{
    	for (Map.Entry<UserData, ChatServerThread> entry : threadMap.entrySet()) {
    		if (entry.getValue() != null) {
    			sendToUser(msg, entry.getKey());
    		}
    	}
    }
    
    public void processMessage(ChatMessage msg, UserData user) {
    	//is called by ChatServerThread class
    	controller.processMessage(msg, user);
    }
    
    public void login(UserData user, ChatServerThread thread) throws IOException {
    	//is called by the ChatServerThread class
    	ChatMessage loginMsg = controller.login(user);
    	if (loginMsg.type == MessageType.LOGIN) {
    		threadMap.put(user, thread);
    		//from here on, messages can be sent to client
    	} else if (loginMsg.type == MessageType.REJECT) {
    		
    	}
    	thread.send(loginMsg);
    	
    	//TODO remove this hack:
    	if (loginMsg.type == MessageType.LOGIN)
    		controller.join(user, controller.defaultRoom);
    }
    
    public void logout(UserData user) {
    	//is called by ChatServerThread class
    	controller.logout(user);
    	threadMap.remove(user);
    }
    
    public void shutdown() {
        try {
            listener.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void kickUser(UserData user) throws IOException {
		if (threadMap.containsKey(user)) {
			threadMap.get(user).logout();
			threadMap.remove(user);
		}
	}

	public void join(ChatMessage input, UserData client) {
		controller.join(client, input.toString());
	}
}
