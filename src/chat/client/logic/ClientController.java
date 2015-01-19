package chat.client.logic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import chat.client.swing.ClientGUI;
import chat.client.swing.ClientGUIListener;
import chat.common.ChatMessage;
import chat.common.ChatMessage.MessageType;

public class ClientController {
	//TODO write join() method to join another room
	//TODO write logout() method for voluntary logout
	//TODO send chat message when user hits enter (key listener)
	
	private final ChatClient client;
	private final ClientGUI gui;
	private boolean logStatus;
	private static final int port = 1234;
	private static final String ip = "localhost";
	
	public ClientController() {
		client = new ChatClient(this, ip, port);
		gui = new ClientGUI();
		new ClientGUIListener(this, gui);
		logStatus = false;
		
		print("[CLIENT] Connection to server established.");
	}

	public void requestLogin() {
		if (logStatus) {
			JOptionPane.showMessageDialog(gui, "You're already logged in!", "Login status", JOptionPane.ERROR_MESSAGE);
		} else {
			//prepare an OptionDialog
	    	String[] options = {"OK", "Cancel"};
	    	
	    	JLabel usernameLabel = new JLabel("Username");
	    	JTextField textField = new JTextField(10);
	    	
	    	JLabel passwordLabel = new JLabel("Password");
	    	JPasswordField passwordField = new JPasswordField(10);
	    	
	    	JPanel panel = new JPanel();
	    	panel.add(usernameLabel);
	    	panel.add(textField);
	    	panel.add(passwordLabel);
	    	panel.add(passwordField);
	    	
	    	textField.requestFocus();
	    	
	    	int clickedOption = 
	    		JOptionPane.showOptionDialog(
	    			gui,
	    			panel,
	    			"Login",
	    			JOptionPane.NO_OPTION,
	    			JOptionPane.PLAIN_MESSAGE,
	    			null,
	    			options,
	    			options[0]
	    				);
	    	
	    	
	    	
	    	if (clickedOption == JOptionPane.OK_OPTION) {
	    		String userName =textField.getText();
	    		if (!userName.contains("[") && !userName.contains("]")) {
			        List<String> userData = new ArrayList<String>();
			        userData.add(userName);
			        userData.add(new String(passwordField.getPassword())); //getPassword returns char[]
			        client.send(new ChatMessage(MessageType.LOGIN, userData));
	    		}
	    		else { //name contains illegal [ ]
	    			JOptionPane.showMessageDialog(gui, "Username must not contain '[' or ']'");
	    		}
		    }
		}
    }
	
	public void updateUserList(ChatMessage msg) {
		msg.contentList.remove(0); //removes the command identifier string
		gui.userListModel.clear();
		for (String userName : msg.contentList) {
			gui.userListModel.addElement(userName);
		}	
	}
	
	public void updateRoomList(ChatMessage msg) {
		msg.contentList.remove(0); //removes the command identifier string
		gui.roomListModel.clear();
		for (String roomName : msg.contentList) {
			gui.roomListModel.addElement(roomName);
		}		
	}

	public void print(String msg) {
		gui.print(msg);
	}

	public void logout() {
		client.logout();
		print("[CLIENT] You logged out.");
		logStatus = false;
	}
	
	public void disconnect() {
		client.shutdown();
	}

	public void send(ChatMessage msg) {
		if (logStatus) 
			client.send(msg);
	}

	public void exit() {
		client.shutdown();		
	}

	public void join(String selectedRoom) {
		send(new ChatMessage(MessageType.JOIN, selectedRoom));
	}

	public boolean connect() {
		return client.connect(ip, port);
	}

	public void setLogStatus(boolean b) {
		logStatus = b;
	}	
}
