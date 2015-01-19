package chat.server.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import chat.common.ChatMessage;
import chat.common.UserData;

public class ChatServerThread extends Thread {
    private Socket socket;
    private ChatServer server;
    
    private UserData client;
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private ChatMessage input;
    
    private volatile boolean running;
    
    public void run() {
    	//check input stream for messages from the client and react
        running = true;
        
        while(running) {;
            try {
                input = read();
            } catch (Exception e) {
                System.err.println("Error reading from stream.\n");
                terminate();
                break;
            }
            
            switch(input.type) {
                case CHAT:
                    server.processMessage(input, client);
                    break;
                    
                case LOGIN:
                	System.err.println("Received login demand.\n");
                	client = new UserData(input.contentList.get(0), input.contentList.get(1));
					try {
						server.login(client, this);
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println("Error at login.\n");
					}
                    break;
                    
                case LOGOUT: //client requests logout
                	logout();
                	break;
                
                case JOIN:
                	server.join(input, client);
                	break;
                    
                default:
                    System.err.println("Incompatible message type at ChatServerThread.run()\n");
                    break;
            }                
        }
        System.err.print("Closing socket...");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket.");
            e.printStackTrace();
        }
        System.err.print("OK\n");
    }
    
    public ChatServerThread (Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private ChatMessage read() throws Exception {
        return (ChatMessage) this.in.readObject();
    }
    
    public void send(ChatMessage m) throws IOException{
        this.out.writeObject(m);
    }
    
    public void logout() {
    	server.logout(client);
    }
    
    private void terminate() {
    	
    }
}
