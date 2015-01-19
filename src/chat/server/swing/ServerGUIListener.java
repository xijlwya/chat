package chat.server.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chat.common.ChatRoom;
import chat.server.logic.ServerController;

public class ServerGUIListener implements ActionListener, ListSelectionListener {
	private ServerController controller;
	private ServerGUI gui;
	
	private String selectedUser;
	private ChatRoom selectedRoom;
	
	private JList<String> userList;
	private JList<ChatRoom> roomList;
	
	public ServerGUIListener(ServerController sc, ServerGUI g) {
		controller = sc;
		gui = g;
		selectedUser = null;
		selectedRoom = null;
		userList = gui.userList;
		roomList = gui.roomList;
		
		gui.registerListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		switch (action) {
			case "createRoom":
				String roomName = JOptionPane.showInputDialog("Create new room:");
				if (roomName.isEmpty()) {
					JOptionPane.showMessageDialog(gui, "No empty names allowed.", "Input Error", JOptionPane.WARNING_MESSAGE);
				} else if (roomName != null) {
					controller.makeRoom(new ChatRoom(roomName));

					//TODO remove this hack
					//sendRoomList() cannot be in makeRoom(), because makeRoom is called, before the server is initialised
					controller.sendRoomList();
				}
				break;
			case "editRoom":
				if (selectedRoom == null)
					JOptionPane.showMessageDialog(gui, "No Room selected!", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else {
					String newName = JOptionPane.showInputDialog("Enter new name for room " + selectedRoom.name, selectedRoom.name);
					if (newName.isEmpty())
						JOptionPane.showMessageDialog(gui, "No empty names allowed.", "Input Error", JOptionPane.WARNING_MESSAGE);
					else if (newName != null) {
						controller.renameRoom(selectedRoom, newName);
					}
				}
				break;
			case "deleteRoom":
				if (selectedRoom == null)
					JOptionPane.showMessageDialog(gui, "No Room selected!", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else if (selectedRoom.equals(controller.defaultRoom))
					JOptionPane.showMessageDialog(gui, controller.defaultRoom.name + " cannot be deleted.", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else {
					controller.deleteRoom(selectedRoom);
				}
				break;
			case "warnUser":
				if (selectedUser == null)
					JOptionPane.showMessageDialog(gui, "No User selected!", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else {
					controller.warnUser(selectedUser);
				}
				break;
			case "kickUser":
				if (selectedUser == null)
					JOptionPane.showMessageDialog(gui, "No User selected!", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else {
					try {
						controller.kickUser(selectedUser);
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(gui, "Could not kick user.", "Selection Error", JOptionPane.WARNING_MESSAGE);
					}
				}
				break;
			case "banUser":
				if (selectedUser == null)
					JOptionPane.showMessageDialog(gui, "No User selected!", "Selection Error", JOptionPane.WARNING_MESSAGE);
				else {
					try {
						controller.banUser(selectedUser);
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(gui, "Could not ban user.", "Selection Error", JOptionPane.WARNING_MESSAGE);
					}
				}
				break;
			case "exitServer":
				//TODO exit the server
				break;
			case "showBannedUsers":
				if (controller.getBanList().isEmpty()) {
					JOptionPane.showMessageDialog(gui, "Ban list is empty!", "List of banned users", JOptionPane.WARNING_MESSAGE);
				} else JOptionPane.showMessageDialog(gui, controller.getBanList(), "List of banned users", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "showRegisteredUsers":
				if (controller.getRegisteredUserList().isEmpty()) {
					JOptionPane.showMessageDialog(gui, "No users registered yet!", "List of registered users", JOptionPane.WARNING_MESSAGE);
				} else JOptionPane.showMessageDialog(gui, controller.getRegisteredUserList(), "List of registered users", JOptionPane.INFORMATION_MESSAGE);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			if (e.getSource() == userList && userList.getSelectedIndex() != -1) {
				//String format userName [roomName] must be split
				selectedUser = userList.getSelectedValue().split(" \\[", 2)[0];
			} else if (e.getSource() == roomList && roomList.getSelectedIndex() != -1) {
				selectedRoom = roomList.getSelectedValue();
			}
		}
		
	}

}
