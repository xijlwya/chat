package chat.client.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import chat.common.ChatMessage;
import chat.common.ChatMessage.MessageType;

public class ChatClient {
	private Socket server;
	private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatClientListenerThread listenerThread;
    
    public volatile boolean processing;

    public ChatClient(ClientController controller, String ip, int port) {
    	try {
			server = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();	
		}
    	
        try {
        	out = new ObjectOutputStream(server.getOutputStream());
			in = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        listenerThread = new ChatClientListenerThread(controller, this.in);
        listenerThread.start(); //liest Serverinput

    }

    public void send(Object m) {
        try {
            this.out.writeObject(m);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
        	e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

	public void logout() {
		send(new ChatMessage(MessageType.LOGOUT));
	}

	public boolean connect(String ip, int port) {
		if (!server.isConnected()) {
			try {
				server = new Socket(ip, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		} else return false;
	}

}

