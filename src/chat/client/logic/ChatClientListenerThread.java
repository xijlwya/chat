package chat.client.logic;

import java.io.IOException;
import java.io.ObjectInputStream;

import chat.common.ChatMessage;

public class ChatClientListenerThread extends Thread {
    private ObjectInputStream clientIn;
    public volatile boolean running;
    
    private ChatMessage fromServer;
	private ClientController controller;
    
    public void run() {
        running = true;
        while (running) {
            try {
                fromServer = read();
            } catch (IOException e) {
                //socket is closed or broken
                //e.printStackTrace();
                running = false;
                continue;
            }
            //do something with the message
            switch(fromServer.type) {
            	case CHAT:
                    controller.print(fromServer.toString());
                    break;
                
            	case REJECT:
            		controller.print("[SERVER] Login rejected: " + fromServer.toString());
            		controller.disconnect();
            		break;
            		
            	case ANNOUNCE:
            		controller.print("[SERVER]: " + fromServer.toString());
            		break;
                    
                case UPDATE: //available Client Commands here
                	switch(fromServer.toString()) {
                		case "roomlist":
                			controller.updateRoomList(fromServer);
                			break;
                		case "userlist":
                			controller.updateUserList(fromServer);
                			break;
                	}
                    break;
                
                case LOGIN:
                	controller.print("[SERVER] You've been sucessfully logged in: " + fromServer.toString());
                	controller.setLogStatus(true);
                	break;
                	
                default:
                    System.err.println("Received message with incompatible type.");
                    break;
            }
        }
        controller.print("[CLIENT] Connection to Server terminated.");
    }
    
    public ChatClientListenerThread(ClientController controller, ObjectInputStream clientIn) {
        this.clientIn = clientIn;
        this.controller = controller;
    }
    
    private ChatMessage read() throws IOException{
        try {
			return (ChatMessage) clientIn.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
}
