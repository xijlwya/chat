package chat.server.swing;

import chat.common.ChatModel;
import chat.common.ChatRoom;
import chat.common.UserData;
import chat.server.logic.ServerController;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.text.DefaultCaret;

public class ServerGUI extends JFrame{
	private static final long serialVersionUID = -602287934921140370L;
	
	private ChatModel model;
	
	public final JTextArea textArea;
	public final JList<String> userList;
	public final JList<ChatRoom> roomList;
	public final DefaultListModel<String> userListModel;
	public final DefaultListModel<ChatRoom> roomListModel;
	
	private List<JMenuItem> registerActionListenerList;
	
	@SuppressWarnings("rawtypes")
	private List<JList> registerListListenerList;
	
    @SuppressWarnings("rawtypes")
	public ServerGUI(ServerController sc, ChatModel model) {
		this.model = model;
    	/*
		 *create all GUI elements
		 */
		//the frame
		this.setTitle("ServerServant");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout(10,10));
		
		//frame.setPreferredSize(new Dimension(500,500));
		this.setMinimumSize(new Dimension (700,500));
		
		//the text area to display chat activities
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane textScroll = new JScrollPane(textArea);
		textScroll.setBorder(
		    BorderFactory.createCompoundBorder(
		        BorderFactory.createTitledBorder("Server Activities"),
		        BorderFactory.createEmptyBorder(5,5,5,5)));
		
		this.add(textScroll, BorderLayout.CENTER);
		
		//the room and user list
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		
		userListModel = new DefaultListModel<String>();
		roomListModel = new DefaultListModel<ChatRoom>();
		
		userList = new JList<String>(userListModel);
		roomList = new JList<ChatRoom>(roomListModel);
		
		JScrollPane userScroll = new JScrollPane(userList);
		JScrollPane roomScroll = new JScrollPane(roomList);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		userScroll.setBorder(
		    BorderFactory.createCompoundBorder(
		        BorderFactory.createTitledBorder("Users"),
		        BorderFactory.createEmptyBorder(5,5,5,5)));
		roomScroll.setBorder(
		    BorderFactory.createCompoundBorder(
		        BorderFactory.createTitledBorder("Rooms"),
		        BorderFactory.createEmptyBorder(5,5,5,5)));
		userScroll.setPreferredSize(new Dimension(150,100));
		right.add(userScroll);
		right.add(roomScroll);
		
		this.add(right, BorderLayout.LINE_END);
		
		//top menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu serverMenu = new JMenu("Server");
		menuBar.add(serverMenu);
		JMenu userMenu = new JMenu("Users");
		menuBar.add(userMenu);
		JMenu roomMenu = new JMenu("Rooms");
		menuBar.add(roomMenu);
		
		//server menu
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setActionCommand("exitServer");
		serverMenu.add(exitItem);
		
		//users menu
		JMenuItem warnItem = new JMenuItem("Warn selected user");
		warnItem.setActionCommand("warnUser");
		JMenuItem kickItem = new JMenuItem("Kick selected user");
		kickItem.setActionCommand("kickUser");
		JMenuItem banItem = new JMenuItem("Ban selected user");
		banItem.setActionCommand("banUser");
		userMenu.add(warnItem);
		userMenu.add(kickItem);
		userMenu.add(banItem);
		userMenu.addSeparator();
		JMenuItem showRegisteredItem = new JMenuItem("Show registered users");
		showRegisteredItem.setActionCommand("showRegisteredUsers");
		JMenuItem showBannedItem = new JMenuItem("Show banned users");
		showBannedItem.setActionCommand("showBannedUsers");
		userMenu.add(showRegisteredItem);
		userMenu.add(showBannedItem);
		
		//rooms menu
		JMenuItem createItem = new JMenuItem("Create new room");
		createItem.setActionCommand("createRoom");
		JMenuItem editItem = new JMenuItem("Edit selected room");
		editItem.setActionCommand("editRoom");
		JMenuItem deleteItem = new JMenuItem("Delete selected room");
		deleteItem.setActionCommand("deleteRoom");
		roomMenu.add(createItem);
		roomMenu.add(editItem);
		roomMenu.add(deleteItem);
		
		this.add(menuBar, BorderLayout.PAGE_START);
		
		/* 
		 * Register Listener
		 */
		
		registerActionListenerList = new ArrayList<JMenuItem>();
		registerActionListenerList.add(exitItem);
		registerActionListenerList.add(deleteItem);
		registerActionListenerList.add(editItem);
		registerActionListenerList.add(createItem);
		registerActionListenerList.add(showBannedItem);
		registerActionListenerList.add(showRegisteredItem);
		registerActionListenerList.add(banItem);
		registerActionListenerList.add(kickItem);
		registerActionListenerList.add(banItem);
		registerActionListenerList.add(warnItem);
		
		registerListListenerList = new ArrayList<JList>();
		registerListListenerList.add(roomList);
		registerListListenerList.add(userList);
		
		this.pack();
		this.setVisible(true);
    }
    
    public void print(String msg) {
    	textArea.append(msg + "\n");
		this.repaint();
    }
    
    @Override
    public void repaint() {
    	//has to be called whenever some change in the model occurs!
    	userListModel.clear();
    	for (Map.Entry<UserData,ChatRoom> entry : model.userRoomMap.entrySet()) {
    		userListModel.addElement(entry.getKey().name + " ["+entry.getValue().name+"]");
    	}
    	
    	roomListModel.clear();
    	for (ChatRoom room : model.roomList) {
    		roomListModel.addElement(room);
    	}
    	
    	super.repaint();
    }
    
    @SuppressWarnings("rawtypes")
    public void registerListener(ServerGUIListener listener) {
    	for (JMenuItem comp : registerActionListenerList) {
    		comp.addActionListener(listener);
    	}
    	for (JList comp : registerListListenerList) {
    		comp.addListSelectionListener(listener);
    	}
    }
    
    
}