package chat.client.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame {
	
	private static final long serialVersionUID = -4102975621750554288L;
	
	public JTextField textField;	
	private JTextArea textArea;
	public DefaultListModel<String> userListModel;
	public DefaultListModel<String> roomListModel;
	public JList<String> roomList;
	private ArrayList<AbstractButton> actionListenerList;
	
	
    public ClientGUI() {
        /*
         *create all GUI elements
         */
    	
        //the frame
        this.setTitle("Chatterbox");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10,10));
        
        this.setPreferredSize(new Dimension(700,500));
        
        //the bottom row with messagebox and send button
        JPanel bottom = new JPanel(new BorderLayout(10,0));
        textField = new JTextField();
        
        JButton sendButton = new JButton("Send");
        sendButton.setActionCommand("send");
        bottom.add(textField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.LINE_END);
        
        this.add(bottom, BorderLayout.PAGE_END);
        
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
                BorderFactory.createTitledBorder("Chat"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        
        this.add(textScroll, BorderLayout.CENTER);
        
        //the room and user list
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
        
        userListModel = new DefaultListModel<String>();
        roomListModel = new DefaultListModel<String>();
        
        JList<String> userList = new JList<String>(userListModel);
        roomList = new JList<String>(roomListModel);
        JScrollPane userScroll = new JScrollPane(userList);
        JScrollPane roomScroll = new JScrollPane(roomList);
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
        JMenu clientMenu = new JMenu("Client");
        JMenu roomMenu = new JMenu("Rooms");
        menuBar.add(clientMenu);
        menuBar.add(roomMenu);
        
        //Client menu
        JMenuItem connectItem = new JMenuItem("Connect");
        connectItem.setActionCommand("connect");
        JMenuItem loginItem = new JMenuItem("Login");
        loginItem.setActionCommand("login");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setActionCommand("logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setActionCommand("exit");
        
        clientMenu.add(connectItem);
        clientMenu.add(loginItem);
        clientMenu.add(logoutItem);
        clientMenu.addSeparator();
        clientMenu.add(exitItem);
        
        //Rooms menu
        JMenuItem joinRoomItem = new JMenuItem("Join selected room");
        joinRoomItem.setActionCommand("join");
        
        roomMenu.add(joinRoomItem);
        
        this.add(menuBar, BorderLayout.PAGE_START);
        
        //listener list
    	actionListenerList = new ArrayList<AbstractButton>();
        
        actionListenerList.add(exitItem);
        actionListenerList.add(joinRoomItem);
        actionListenerList.add(logoutItem);
        actionListenerList.add(loginItem);
        actionListenerList.add(roomMenu);
        actionListenerList.add(clientMenu);
        actionListenerList.add(sendButton);
        
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void repaint() {
    	super.repaint();
    }

	public void print(String msg) {
		textArea.append(msg + "\n");
		this.repaint();
	}

	public void registerListener(ClientGUIListener clientGUIListener) {
		for (AbstractButton comp : actionListenerList)
			comp.addActionListener(clientGUIListener);
		this.addWindowListener(clientGUIListener);
		roomList.addListSelectionListener(clientGUIListener);
		textField.addKeyListener(clientGUIListener);
	}
}
