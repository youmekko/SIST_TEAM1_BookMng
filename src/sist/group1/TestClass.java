package sist.group1;

import java.util.*;

public class TestClass {

	/*
	 * @Param testData Set<String>key = TestClass.testBook.keySet(); Iterator it =
	 * key.iterator(); while(it.hasNext()){ String key = (String)it.next(); Book b =
	 * TestClass.testBook.get(key); }
	 */
	public static final Map<String, Book> testBook = new HashMap<String, Book>();
	public static final Map<String, User> testUser = new HashMap<String, User>();

	static {

		testBook.put("B001", new Book("B001", "Java Programming", "T H Kim", "Soul"));
		testBook.put("B002", new Book("B002", "C Programming", "S H Park", "Hanbit"));
		testBook.put("B003", new Book("B003", "TCP/IP Window Socket Programming", "S J Moon", "Hanbit"));
		testBook.put("B004", new Book("B004", "Oracle 2007", "S H KIM", "Orange Media"));
		testBook.put("B005", new Book("B005", "MS Office 2010", "D H Ahn", "Orange Media"));

		testUser.put("U001", new User("U001", "clear219", "anstjdwo1234", "SeongJaeMoon", "010-4318-0221"));
		testUser.put("U002", new User("U002", "sjmoon0221", "anstjdwo1234", "SeongjaeMoon", "010-4318-0221"));
		testUser.put("U003", new User("U003", "minjong12", "anstjdwo1234", "Minjong", "010-1234-1234"));
		testUser.put("U004", new User("U004", "asdf123", "anstjdwo1234", "DonghyunAhn", "010-2233-0121"));
		testUser.put("U005", new User("U005", "choi123", "anstjdwo1234", "YumiChoi", "010-1234-4567"));

	}
}
