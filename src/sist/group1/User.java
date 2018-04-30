package sist.group1;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable, Comparable<User> {

	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * @Param 고유번호(ex. U001, U002...), 아이디, 비밀번호, 이름, 전화번호, 받은 메세지, 유저 고유의 대출 정보
	 */
	private String userNo;
	private String userId;
	private String password;
	private String name;
	private String phone;
	private List<String> messages = new ArrayList<String>();

	public User() {

	}

	public User(String userNo, String userId, String password, String name, String phone) {
		this.userNo = userNo;
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.phone = phone;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getphone() {
		return phone;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages.add(messages);
	}

	@Override
	public String toString() {
		return String.format("%s/%s/%s/%s", this.userNo, this.name, this.userId, this.phone);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof User) {
			User user = (User) obj;
			if (this.userId.equals(user.getUserId()) && this.userId.equals(user.getPassword())) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + this.userId.hashCode();
		result = 31 * result + this.password.hashCode();
		return result;
	}

	@Override
	public int compareTo(User user) {
		return this.userId.compareTo(user.getUserId());
	}
}
