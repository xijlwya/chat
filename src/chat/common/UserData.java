package chat.common;

import java.io.*;

public class UserData implements Serializable{

	private static final long serialVersionUID = 1L;

	public String name;
    public String password;
    //~ private ChatServerThread clientThread;
    
    public UserData(String username, String password) {
        name = username;
        this.password = password;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserData other = (UserData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
    
    @Override
    public String toString() {
		return name;
    }
}
