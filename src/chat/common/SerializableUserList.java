package chat.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class SerializableUserList implements Serializable {
	private static final long serialVersionUID = 8821311961674353321L;
	
	private List<UserData> userList;
	private File file;
	
	public SerializableUserList(String fileName) {
		file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readUserListFromFile();
	}
	
	public void readUserListFromFile() {
		ObjectInputStream fileInput = null;
		
		try {
			fileInput = new ObjectInputStream(new FileInputStream(file));
			setUserList(((SerializableUserList) fileInput.readObject()).getUserList());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		finally {
			try {
				fileInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writeUserListToFile() {
		ObjectOutputStream fileOutput = null;
		try {
			fileOutput = new ObjectOutputStream(new FileOutputStream(file));
			fileOutput.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the userList
	 */
	public List<UserData> getUserList() {
		return userList;
	}

	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<UserData> userList) {
		this.userList = userList;
	}
}
