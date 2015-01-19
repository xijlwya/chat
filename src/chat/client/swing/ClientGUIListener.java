package chat.client.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chat.client.logic.ClientController;
import chat.common.ChatMessage;

public class ClientGUIListener extends WindowAdapter implements ActionListener, ListSelectionListener, KeyListener {
	
	private ClientController controller;
	private ClientGUI gui;
	public String selectedRoom;
	
	public ClientGUIListener(ClientController c, ClientGUI gui) {
		this.controller = c;
		this.gui = gui;
		
		gui.registerListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "send":
			controller.send(new ChatMessage(gui.textField.getText()));
			gui.textField.setText("");
			gui.textField.requestFocus();
			break;
		case "login":
			controller.requestLogin();
			break;
		case "logout":
			controller.logout();
			gui.roomListModel.clear();
			gui.userListModel.clear();
			break;
		case "exit":
			controller.exit();
			break;
		case "join":
			controller.join(selectedRoom);
			break;
		case "connect":
			if(!controller.connect())
				JOptionPane.showMessageDialog(gui, "This client is already connected!", "Connection", JOptionPane.WARNING_MESSAGE);
			break;
		}
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		controller.logout();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting() && e.getSource() == gui.roomList && gui.roomList.getSelectedIndex() != -1) {
			selectedRoom = gui.roomList.getSelectedValue();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(gui.textField.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			controller.send(new ChatMessage(gui.textField.getText()));
			gui.textField.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		//do nothing
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		//do nothing
	}
}
