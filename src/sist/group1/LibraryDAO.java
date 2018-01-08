package sist.group1;

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;

public class LibraryDAO {

	/*
	 * 
	 * this.users.add(new User("","","","","")); 전체 사용자 정보 저장용 리스트에 넣는 방법
	 * this.books.add(new Book("","","","")); 전체 책 정보 저장용 리스트에 넣는 방법
	 * this.checkOuts.add(new CheckOut("B001","U001","2017-12-27")); 대출 전체 정보 리스트에
	 * 넣는 방법 for(CheckOut c : checkOuts) { 리스트에서 특정 매개변수 값에 맞게 객체 정보 꺼내오는 방법
	 * if(c.getcBookNo().equals("B001")) {
	 * 
	 * } Set<String>key = TestClass.testBook.keySet(); Iterator it = key.iterator();
	 * while(it.hasNext()){ 맵에서 특정 매개변수 값에 맞게 객체 정보 꺼내오는 방법 String key =
	 * (String)it.next(); Book b = TestClass.testBook.get(key); }
	 * 
	 * }
	 */

	/*
	 * @Param 사용자 정보(현재 접속한 사용자 아이디, 책 정보), 사용자 전체 정보, 책 전체 정보, 대출 전체 정보
	 */

	private Utils utils = Utils.getInstance();
	// private User currentUser = this.utils.getCurrentUser()();
	
	//사용자 정보, 도서 정보, 도서 대출 정보를 저장할 컬렉션 선언
	private Map<String, User> users = new HashMap<String, User>();
	private Map<String, Book> books = new HashMap<String, Book>();
	private List<CheckOut> checkOuts = new ArrayList<CheckOut>();

	//프로그램 종료시 D드라이브에서 파일 저장 및 실행시 파일 불러오기 위한 경로 지정
	private static final String USER_FILE = "D:\\users.data"; //사용자 정보를 저장할 파일 경로 및 파일 이름
	private static final String BOOK_FILE = "D:\\books.data"; //도서 정보를 저장할 파일 경로 및 파일 이름
	private static final String CHECKOUT_FILE = "D:\\checkOuts.data"; //도서 대출 정보를 저장할 파일 경로 및 파일 이름

	private LocalDate now = LocalDate.now();
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String nowDate = this.now.format(dateFormat);

	//파일 불러오기를 위한 메소드 선언
	@SuppressWarnings("unchecked") //obj 캐스팅 컴파일 경고를 사용하지 않도록 설정
	public LibraryDAO() {
		/*사용자, 도서, 대출 정보 파일 객체 생성*/
		File file = new File(USER_FILE);
		File file1 = new File(BOOK_FILE);
		File file2 = new File(CHECKOUT_FILE);
		Object obj = null;
		//사용자 정보 파일이 존재 할 경우라면
		if (file.exists()) {
			//역직렬화 해서 파일 가져오기
			obj = deSerialization(USER_FILE);
			//역직렬화한 파일을 사용자 정보를 저장한 저장소에 할당
			this.users = (Map<String, User>) obj;
		}
		//도서 정보 파일이 존재 할 경우라면
		if (file1.exists()) {
			//역직렬화 해서 파일 가져오기
			obj = deSerialization(BOOK_FILE);
			//역직렬화한 파일을 사용자 정보를 저장한 저장소에 할당
			this.books = (Map<String, Book>) obj;
		}
		//대출 정보 파일이 존재 할 경우라면
		if (file2.exists()) {
			//역직렬화 해서 파일 가져오기
			obj = deSerialization(CHECKOUT_FILE);
			//역직렬화한 파일을 사용자 정보를 저장한 저장소에 할당
			this.checkOuts = (List<CheckOut>) obj;
		}
	}

	/*
	 * -회원가입이 완료 되었습니다. 
	 * -이미 존재하는 id 입니다. 다시 입력해주세요. 
	 * -잘못된 비밀번호 형식입니다. 다시 입력해주세요. 
	 * -잘못된 전화번호 형식입니다. 다시 입력해주세요.
	 */

	//회원가입을 통한 사용자 등록 메소드 선언
	//@Param 사용자 아이디, 비밀번호, 이름, 전화번호
	public void register(String userId, String password, String name, String phone) {
		//User 타입의 임시 변수 선언
		User user = null;
		//사용자 고유번호 String 변수 선언 (자동생성)
		String userNo = "U001";
		//이미 등록된 사용자가 있다면	
		if (this.users.size() > 0) {
			Set<String> key = this.users.keySet();
			List<String> temp = new ArrayList<String>(key);
			//사용자 등록번호(키)를 기준으로 오름차순 정렬 (맵 타입의 변수는 값이 정렬되어 있지 않기 때문에 바로 값을 뽑아올 수 없다.)
			Collections.sort(temp, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return s1.compareTo(s2);
				}
			});
			//고유 번호를 기준으로 가장 마지막 사용자를 가져오기 위한 키 임시 변수 선언
			String tempKey = temp.get(temp.size() - 1); 
			//가장 마지막 사용자 객체 가져오기
			user = this.users.get(tempKey);
			//가장 마지막 등록번호를 가진 사용자 고유번호(키)+1한 값을 고유번호 변수에 할당
			userNo = String.format("U%03d", Integer.parseInt(user.getUserNo().substring(1)) + 1);
		}
		//사용자 객체 생성
		User u = new User(userNo, userId, password, name, phone);
		//사용자 고유번호(키), 사용자 객체를 사용자 저장소에 저장
		this.users.put(userNo, u);
	}

	//관리자의 도서 등록 메소드 선언
	//@Param 도서명, 저자, 출판사
	public String registerBook(String bookTitle, String author, String publisher) {
		//Book 타입의 임시 변수 선언
		Book book = null;
		//도서 고유번호 String 변수 선언 (자동생성)
		String bookNo = "B001";
		if (this.books.size() > 0) {
			Set<String> key = this.books.keySet();
			List<String> temp = new ArrayList<String>(key);
			//도서 등록번호(키)를 기준으로 오름차순 정렬 (맵 타입의 변수는 값이 정렬되어 있지 않기 때문에 바로 값을 뽑아올 수 없다.)
			Collections.sort(temp, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return s1.compareTo(s2);
				}
			});
			//도서 번호를 기준으로 가장 마지막 도서를 가져오기 위한 키 임시 변수 선언
			String tempKey = temp.get(temp.size() - 1);
			//가장 마지막 도서 객체 가져오기
			book = this.books.get(tempKey);
			//가장 마지막 등록번호를 가진 도서 고유번호(키)+1한 값을 고유번호 변수에 할당
			bookNo = String.format("B%03d", Integer.parseInt(book.getBookNo().substring(1)) + 1);
		}
		//도서 고유번호(키), 도서 객체를 사용자 저장소에 저장
		this.books.put(bookNo, new Book(bookNo, bookTitle, author, publisher));
		//도서 고유번호(키)반환 (사용자에게 보여주기 위해)
		return bookNo;
	}

	//사용자 객체 저장소에 존재 여부 확인용 메소드 선언
	//@Param 사용자 아이디, 사용자 비밀번호 범용적으로 사용 가능한 메소드. (현재 사용자 설정용, 사용자 존재 여부)
	public User getCurrentUser(String userId, String password) {
		//결과 값으로 넘길 User 타입 변수 생성
		User user = null;
		//전체 users의 반복문을 돌리며, 탐색하기 위해 set, list 생성
		Set<String> key = this.users.keySet();
		List<String> temp = new ArrayList<String>(key);
		//Iterator로 반복문 돌리기
		Iterator<String> it = temp.iterator();
		while (it.hasNext()) {
			String keyTemp = (String) it.next();
			//User 타입의 임시 변수 선언 후 users 맵 객체의 값 할당
			User u = this.users.get(keyTemp);
			//매개변수로 입력받은 사용자 아이디 및 비밀번호가 일치하는 값이 있다면
			if (u.getUserId().equals(userId) && u.getPassword().equals(password)) {
				//결과 값을 리턴할 유저 변수에 할당
				user = u;
				//결과 값을 찾았으므로, 더 이상 반복문을 돌릴 필요가 없다. break;
				break;
			}
		}
		//결과 값 리턴 -> 결과 값이 null이라면, 사용자가 없다는 뜻이다. 가져다 쓸 때 널 체크 필수!
		return user;
	}

	/*
	 * 사용자 아이디 존재 여부 확인
	 * 
	 * @Param 사용자 아이디, 사용자 비밀번호 아이디는 존재하지만, 비밀번호가 맞지 않을 경우 true를 리턴한다.
	 */
	public boolean isWrongPassword(String userId, String password) {
		boolean result = false;
		Set<String> key = this.users.keySet();
		List<String> temp = new ArrayList<String>(key);
		Iterator<String> it = temp.iterator();
		while (it.hasNext()) {
			String keyTemp = (String) it.next();
			User u = this.users.get(keyTemp);
			if (u.getUserId().equals(userId) && !u.getPassword().equals(password)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/*
	 * @Param 파일이름 파일 역직렬화 프로그램 실행시 users, books, checkouts 초기화를 위한 메소드
	 */
	//파일 저장용 메소드 선언
	//@Param 파일 이름이 포함된 경로(USER_FILE, BOOK_FILE, CHECKOUT_FILE)
	public Object deSerialization(String fileName) {
		//역직렬화한 객체를 반환하기 위한 Object 변수 선언
		Object result = null;
		//파일을 스트림으로 읽어오기 위한 변수 선언
		FileInputStream fs = null;
		//파일로부터 읽어온 스트림을 원래 필드 형으로 가져오기 위한 변수 선언
		ObjectInputStream os = null;
		try {
			//파일 읽어오기
			fs = new FileInputStream(fileName);
			//읽어온 파일로부터 객체 데이터를 읽어오기
			os = new ObjectInputStream(fs);
			//객체 읽기
			result = os.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				//스트림을 닫아주기
				os.close();
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//읽어온 객체 리턴
		return result;
	}

	//프로그램 종료시 users, books, checkOuts를 파일로 저장을 위한 메소드 선언
	public void logout() {
		//직렬화를 위한 스트림 생성
		FileOutputStream fs = null;
		ObjectOutputStream os = null;
		try {
			//사용자 객체가 저장된 컬렉션의 크기가 0보다 크다면
			if (this.users.size() > 0) {
				//D:\\users.data 파일의 스트림 객체 생성
				fs = new FileOutputStream(USER_FILE);
				os = new ObjectOutputStream(fs);
				// 컬렉션 저장소에 저장된 모든 정보를 직렬화 시도
				os.writeObject(this.users);
			}
			//도서 객체가 저장된 컬렉션의 크기가 0보다 크다면
			if (this.books.size() > 0) {
				//D:\\books.data 파일의 스트림 객체 생성
				fs = new FileOutputStream(BOOK_FILE);
				os = new ObjectOutputStream(fs);
				// 컬렉션 저장소에 저장된 모든 정보를 직렬화 시도
				os.writeObject(this.books);
			}
			//도서 대출 정보 객체가 저장된 HashMap의 크기가 0보다 크다면
			if (this.checkOuts.size() > 0) {
				//D:\\checkOuts.data 파일의 스트림 생성
				fs = new FileOutputStream(CHECKOUT_FILE);
				os = new ObjectOutputStream(fs);
				// 컬렉션 저장소에 저장된 모든 정보를 직렬화 시도
				os.writeObject(this.checkOuts);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//스트림 닫아주기
				os.close();
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//기존 도서 삭제용 메소드 선언
	//@Param 도서 고유번호(키)를 받아와서 해당 객체 삭제  
	public String deleteBook(String bookNo) {
		//삭제 된 도서의 정보를 반환하기 위한 StringBuilder객체 생성
		StringBuilder sb = new StringBuilder();
		//삭제할 도서를 가져올 객체 생성
		Book b = this.books.get(bookNo);
		//삭제할 도서의 정보를 append
		sb.append(String.format("[%s/%s]가 삭제되었습니다.%n", b.getBookNo(), b.getBookTitle()));
		//키를 통해 해당 객체 삭제
		this.books.remove(bookNo);
		//삭제한 도서 정보 반환
		return sb.toString();
	}

	/*
	 * @Param word : Book의 getter 종류 선택할 때 key : 값 비교할 때 검색어와 일치하는 도서를 출력하게 만드는 메소드
	 */
	public String searchForBooks(String word, String key) {

		StringBuilder sb = new StringBuilder();
	
		// 검색어에 해당하는 도서 안내 메세지

		Set<String> set = this.books.keySet();
		List<String> list = new ArrayList<String>(set);
		sb.append("\n");
		sb.append(String.format("[%s]에 해당하는 도서목록 입니다.%n", key));
		sb.append(String.format("------------------------------------%n"));
		sb.append(String.format("등록번호/도서명/저자/출판사/대출현황%n"));
		sb.append(String.format("------------------------------------%n"));

		if (word.equals("등록번호")) {

			Book s = this.books.get(key);
			sb.append(String.format("%s/%s/%s/%s/%s%n", s.getBookNo(), s.getBookTitle(), s.getPublisher(),
					s.getAuthor(), s.getBookStatus()));

		} else if (word.equals("도서명")) {
			for (String i : list) {
				if (this.books.get(i).getBookTitle().contains(key)) {
					sb.append(String.format("%s/%s/%s/%s/%s%n", this.books.get(i).getBookNo(),
							this.books.get(i).getBookTitle(), this.books.get(i).getPublisher(),
							this.books.get(i).getAuthor(), this.books.get(i).getBookStatus()));
				}
			}

		} else if (word.equals("출판사")) {
			for (String i : list) {
				if (this.books.get(i).getPublisher().contains(key)) {
					sb.append(String.format("%s/%s/%s/%s/%s%n", this.books.get(i).getBookNo(),
							this.books.get(i).getBookTitle(), this.books.get(i).getPublisher(),
							this.books.get(i).getAuthor(), this.books.get(i).getBookStatus()));
				}
			}
		}

		else if (word.equals("저자")) {
			for (String i : list) {
				if (this.books.get(i).getAuthor().contains(key)) {
					sb.append(String.format("%s/%s/%s/%s/%s%n", this.books.get(i).getBookNo(),
							this.books.get(i).getBookTitle(), this.books.get(i).getPublisher(),
							this.books.get(i).getAuthor(), this.books.get(i).getBookStatus()));
				}
			}
		}
		sb.append(String.format("------------------------------------"));

		return sb.toString();
	}

	// 대출중 도서 확인
	public String viewCheckedOutBooks() {

		StringBuilder sb = new StringBuilder();
		// 임시 카운트 변수
		int a = 0;
		// 퍼센트 계산용 books 사이즈 변수
		double d = this.books.size();
		if (this.checkOuts.size() != 0) {
			// 오늘날짜 출력
			sb.append(String.format("오늘 날짜 : %s%n", this.nowDate));
			sb.append(String.format("-------------------------------------------------------------%n"));
			sb.append(String.format("등록번호/도서명/저자/출판사/대출일/반납예정일/대출인/회원번호%n"));
			sb.append(String.format("-------------------------------------------------------------%n"));

			for (CheckOut c : checkOuts) {
				Book b = this.books.get(c.getBookNo());
				//대출중이거나 연체중이거나, 반납예정일이 없거나
				if ((b.getBookStatus().equals("대출중") || b.getBookStatus().equals("연체중")) && !c.getDueDate().equals("")) {

					User s = this.users.get(c.getUserNo());
					++a;
					sb.append(String.format("%s/%s/%s/%s/%s/%s/%s/%s%n", b.getBookNo(), b.getBookTitle(), b.getAuthor(),
							b.getPublisher(), c.getCheckOutDate(), c.getDueDate(), s.getName(), c.getUserNo()));
				}
			}
			sb.append(String.format("-------------------------------------------------------------%n"));

			// 퍼센트 단위 바꿔주는 변수
			
			// 강제 형변환
		}
		if(a<=0) {
			sb.append("대출 중인 책이 없습니다.");
		}else {
			double e = (a / d) * 100.0;
			sb.insert(0, String.format("%n도서관내 [%d%%] 책이 대출중 입니다.%n", (int) e));
		}
		return sb.toString();

	}

	/*
	 * @Param word : Book의 getter 종류 선택할 때 key : 값 비교할 때 검색어와 일치하는 유저를 출력하게 만드는 메소드
	 */
	public String serachForUsers(String word, String key) {

		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append(String.format("[%s]에 해당하는 회원입니다.%n", key));
		sb.append(String.format("--------------------------------------------------%n"));
		sb.append(String.format("회원번호/아이디/이름/전화번호%n"));
		sb.append(String.format("--------------------------------------------------%n"));

		Set<String> set = this.users.keySet();
		List<String> list = new ArrayList<String>(set);

		// 중복 값 출력을 위한 정렬 메소드
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String a1, String b1) {
				return a1.compareTo(b1);
			}
		});

		// User toString 수정해야함
		if (word.equals("회원번호")) {
			sb.append(String.format("%s%n", this.users.get(key).toString()));
		} else if (word.equals("이름")) {
			for (String i : list) {
				if (this.users.get(i).getName().contains(key)) {
					sb.append(String.format("%s%n", this.users.get(i).toString()));
				}
			}
		} else if (word.equals("아이디")) {
			for (String i : list) {
				if (this.users.get(i).getUserId().contains(key)) {
					sb.append(String.format("%s%n", this.users.get(i).toString()));
				}
			}
		} else if (word.equals("전화번호")) {
			for (String i : list) {
				if (this.users.get(i).getphone().contains(key)) {
					sb.append(String.format("%s%n", this.users.get(i).toString()));
				}
			}
		}

		sb.append(String.format("--------------------------------------------------%n"));
		return sb.toString();
	}

	/*
	 * @Param 유저 상세보기.
	 */
	public String viewUserInDetail(String userNo) {
		StringBuilder sb = new StringBuilder();
		// count용 임시변수, 회차
		int count = 0;
		// 값 입력받는 변수

		sb.append("\n");
		sb.append(String.format("[%s]의 상세보기 입니다.%n", this.users.get(userNo)));
		sb.append(String.format("오늘 날짜 : %s%n", this.nowDate));
		sb.append(String.format("--------------------------------------------------%n"));
		sb.append(String.format("회차/도서명/대출일/반납일/반납예정일/연체일수%n"));
		sb.append(String.format("--------------------------------------------------%n"));

		for (CheckOut checkout : checkOuts) {
			if (checkout.getUserNo().equals(userNo)) {
				++count;
				Book book = this.books.get(checkout.getBookNo());
				if (checkout.getReturnDate() == null)
					checkout.setReturnDate("");
				sb.append(String.format("%d/%s/%s/%s/%s/%s%n", count, book.getBookTitle(), checkout.getCheckOutDate(),
						checkout.getReturnDate(), checkout.getDueDate(), checkout.getOverdueDays()));
			}
		}
		sb.append(String.format("--------------------------------------------------%n"));
		return sb.toString();
	}

	// 전체 메세지 보기
	public String viewAllMessages() {
		StringBuilder sb = new StringBuilder();
		try {
			if (this.utils.getCurrentUser().getMessages().size() == 0
					|| this.utils.getCurrentUser().getMessages() == null) {
				sb.append("*************************************\n");
				sb.append("관리자에게서 온 메시지가 없습니다.\n");
				sb.append("*************************************\n");
			} else {
				sb.append("************************************************************\n");
				sb.append(String.format("[%s/%s]님에게 온 메세지가 [%d]개 있습니다.%n", this.utils.getCurrentUser().getUserNo(),
						this.utils.getCurrentUser().getName(), this.utils.getCurrentUser().getMessages().size()));

				for (int i = 0; i < this.utils.getCurrentUser().getMessages().size(); i++) {

					sb.append(String.format("[%d] %s%n", i + 1, this.utils.getCurrentUser().getMessages().get(i)));
				}

				// sb.append(String.format("%n(%d개)연체중인 도서가 있습니다. 반납 해주세요.%n", checkoutNum));
			}
			sb.append("************************************************************");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	// deleteOneMessage (메시지 한개씩 삭제하는 메소드)
	public void deleteOneMessage(int messageNumber) {
		// 가져와야 할 것 : 메세지의 번호(messageNumber)
		this.utils.getCurrentUser().getMessages().remove(messageNumber - 1);
	}

	// 메세지 전체 삭제
	public void deleteAllMessages() {
		this.utils.getCurrentUser().getMessages().clear();
	}

	// 오늘 날짜 구하는 메소드
	public String getToday() {
		String today = this.now.format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
		return today;
	}

	// 오늘 날짜로부터 +7일을 구하느 메소드
	public String getDueday() {
		String dueday = this.now.plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
		return dueday;
	}

	// 도서 상세보기
	public String viewBookInDetail(String bookNo) {
		StringBuilder sb = new StringBuilder();

		Book book = this.books.get(bookNo);

		sb.append(String.format("[%s/ %s/ %s/ &s]%n", book.getBookNo(), book.getBookTitle(), book.getPublisher(),
				book.getAuthor()));
		sb.append(String.format("오늘 날짜 : %s%n", this.getToday()));
		sb.append(String.format("--------------------------------------------------------------%n"));
		sb.append(String.format("회차/대출일/반납일/반납예정일/연체일수/대출인/회원번호%n"));
		sb.append(String.format("--------------------------------------------------------------%n"));

		// 모든 체크아웃 객체들 중에
		// 지금 내가 보려는 책넘버랑 똑같은 책넘버를 가지고 있는 객체
		int count = 0;
		for (CheckOut checkOut : checkOuts) {
			if (checkOut.getBookNo().equals(book.getBookNo())) {
				String key = checkOut.getUserNo();
				++count;
				sb.append(String.format("%d/%s/%s/%s/%d/%s/%s%n", count, checkOut.getCheckOutDate(),
						checkOut.getReturnDate(), checkOut.getDueDate(), checkOut.getOverdueDays(),
						checkOut.getUserNo(), this.users.get(key).getName()));
			}
		}
		sb.append(String.format("--------------------------------------------------------------%n"));
		return sb.toString();
	}

	// checkOutBook 사용자가 도서를 대출 한다.
	public String checkOutBook(String bookNo) {
		StringBuilder sb = new StringBuilder();
		// 매개변수로 받은 대출할 도서의 번호, 현재 로그인한 사용자번호, 오늘 날짜로 체크아웃 객체를 만든다.
		CheckOut checkOut = new CheckOut(bookNo, utils.getCurrentUser().getUserNo(), this.getToday());
		// 해당 체크아웃 객체의 반납예정일을 7일 이후로 설정한다.
		checkOut.setDueDate(this.getDueday());
		// 체크아웃 객체를 checkOuts 리스트에 추가한다.
		this.checkOuts.add(checkOut);
		// 매개변수로 받은 대출할 도서의 도서 번호로 해당 도서를 book 변수에 담은 다음

		Book book = this.books.get(bookNo);
		// status를 (대출중)으로 변경한다.
		book.setBookStatus("대출중");
		sb.append(String.format("[%s/%s]의 대출이 완료되었습니다.", book.getBookNo(), book.getBookTitle()));
		return sb.toString();
	}

	// viewCheckedOutBooks 사용자가 대출중인 도서 목록을 본다.
	public String viewUserCheckedOutBooks() {
		
		this.setAllOverdueDays();
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(String.format("[%s/%s]님의 현재 대출 목록  입니다.%n", this.utils.getCurrentUser().getUserNo(),
				this.utils.getCurrentUser().getName()));
		sb.append(String.format("오늘 날짜 : %s%n", this.getToday()));

		sb.append(String.format("---------------------------------------------------------%n"));
		sb.append(String.format("등록번호/도서명/대출일/반납예정일/대출현황/연체일수%n"));
		sb.append(String.format("---------------------------------------------------------%n"));

		// 체크아웃 리스트가 가진 모든 체크아웃 객체들 중에
		for (CheckOut checkOut : this.checkOuts) {
			// 현재 회원번호와 체크아웃 객체가 가진 회원번호가 같을 때
			if (checkOut.getUserNo().equals(this.utils.getCurrentUser().getUserNo())) {
				// 해당 체크아웃 객체가 가진 도서 등록번호만을 가지고 와서 book 변수에 담고
				Book book = books.get(checkOut.getBookNo());
				// 해당 북이 대출중이거나 연체중이라면
				if (book.getBookStatus().equals("대출중") || book.getBookStatus().equals("연체중")) {
					// StringBuilder에 내용을 붙여준다.
					sb.append(String.format("%s/%s/%s/%s/%s/%d%n", book.getBookNo(), book.getBookTitle(),
							checkOut.getCheckOutDate(), checkOut.getDueDate(), book.getBookStatus(),
							checkOut.getOverdueDays()));
				}
			}

		}
		sb.append(String.format("---------------------------------------------------------%n"));
		return sb.toString();
	}

	// 해당 도서의 상태를 확인
	public int getBookStatus(String bookNo) {
		int result = 0;
		Set<String> key = this.books.keySet();
		List<String> temp = new ArrayList<String>(key);
		Iterator<String> it = temp.iterator();
		while (it.hasNext()) {
			String keyTemp = (String) it.next();
			Book b = this.books.get(keyTemp);
			if (b.getBookNo().equals(bookNo)) {
				result = 1; // 도서 등록번호가 있으면 1
			}
			if (b.getBookNo().equals(bookNo) && (b.getBookStatus().equals("대출중")|| b.getBookStatus().equals("연체중"))) {
				result = 2; // 대출 중이거나 연체 중
			}
		}
		return result; // 등록 번호가 없으면 0
	}

	// 책이 존재하는지 확인
	/*
	 * private void isExistBook(String value) throws BookException{
	 * if(this.getBook(value)==null) { throw new
	 * BookException("등록되지 않은 책 입니다. 다시 입력해주세요."); } }
	 */

	// returnBook 사용자가 도서를 반납 한다.
	// 도서번호를 매개 변수로 받고, 해당하는 책의 bookStatus를 0으로 바꾸고 checkOUt데이터의 반납일을 오늘로 만들어주면 됨.
	public String returnBook(String bookNo) {
		StringBuilder sb = new StringBuilder();
		// 모든 체크아웃 데이터중에
		for (CheckOut checkOut : this.checkOuts) {
			// 체크아웃이 가지고 있는 도서 번호가 매개변수로 받은 반납하고 싶은 책 넘버와 같을 때&&
			// 체크아웃이 가지고 있는 유저 번호가 현재 사용자의 번호와 같다면
			if (checkOut.getBookNo().equals(bookNo)
					&& checkOut.getUserNo().equals(utils.getCurrentUser().getUserNo())) {
				// 매개변수로 받은 반납하고 싶은 책을 book 변수에 담고
				Book book = books.get(bookNo);
				// 그 책의 상태가 0(비치중)이 아니라면
				if (!book.getBookStatus().equals("비치중")) {
					// 책의 상태를 0(비치중)으로 변경하고
					book.setBookStatus("비치중");
					// 해당 체크아웃 객체의 반납일을 오늘로 설정하고, 반납 예정일을 ""로 변경
					checkOut.setReturnDate(getToday());
					checkOut.setDueDate("");
					sb.append(String.format("[%s/%s]이 반납되었습니다.", book.getBookNo(), book.getBookTitle()));
				}
			}
		}
		return sb.toString();
	}

	// viewReturnedBooks 사용자가 반납한 도서 목록을 본다.
	public String viewReturnedBooks() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(String.format("[%s/%s]님의 반납 목록  입니다.%n", this.utils.getCurrentUser().getUserNo(),
				this.utils.getCurrentUser().getName()));
		sb.append(String.format("오늘 날짜 : %s%n", this.getToday()));
		sb.append(String.format("----------------------------------------------------%n"));
		sb.append(String.format("회차/등록번호/도서명/대출일/반납일/연체일수%n"));
		sb.append(String.format("----------------------------------------------------%n"));
		// 체크아웃 리스트를 대출일 기준으로 sort(정렬)기 위한 Collections 클래스의 sort메소드 사용
		Collections.sort(this.checkOuts);
		// 회차 증가를 위한 count변수 선언
		int count = 0;
		// 체크아웃 리스트에 담긴 모든 체크아웃 객체들 중에
		for (CheckOut checkOut : this.checkOuts) {
			// 체크아웃 객체가 가진 회원번호와 현재 사용자의 유저번호가 같고, 체크아웃 객체의 반납일이 Null이 아닐때
			if ((checkOut.getUserNo().equals(this.utils.getCurrentUser().getUserNo()))
					&& (checkOut.getReturnDate() != null)) {
				// 조건문을 만족하는 범위에서 count 증가
				count++;
				// 체크아웃에 객체의 필드인 bookNo를 이용해서 해당 도서를 book 변수에 담고
				Book book = books.get(checkOut.getBookNo());
				// StringBuilder에 내용을 붙여준다.
				sb.append(String.format("%d/%s/%s/%s/%s/%d%n", count, book.getBookNo(), book.getBookTitle(),
						checkOut.getCheckOutDate(), checkOut.getReturnDate(), checkOut.getOverdueDays()));
			}
		}
		sb.append(String.format("----------------------------------------------------%n"));
		return sb.toString();
	}

	// 반납 예정일 수정
	public String changeDueDate(String bookNo, String dueDate) {

		StringBuilder sb = new StringBuilder();
		boolean temp = books.containsKey(bookNo);
		if (temp == false) {
			sb.append("잘못된 형식입니다.");
		} else {
			for (CheckOut d : this.checkOuts) {
				if (bookNo.equals(d.getBookNo()) && (d.getReturnDate() == null || d.getReturnDate().equals(""))) {
					d.setDueDate(dueDate);
					this.books.get(bookNo).setBookStatus("연체중");
					sb.append("반납 예정일 변경이 완료되었습니다.");
				}
			}
		}
		return sb.toString();
	}

	// 전체 도서 출력
	public String viewAllBooks() {
		System.out.println();
		System.out.println("전체 도서 목록 입니다.");
		
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("----------------------------------------------%n"));
		sb.append(String.format("등록번호/도서명/저자/출판사/대출현황%n"));
		sb.append(String.format("----------------------------------------------%n"));

		Set<String> key = this.books.keySet();

		// BOOK의 KEY값을 저장하기 위한 임시변수 LIST b
		List<String> b = new ArrayList<String>(key);

		Collections.sort(b, new Comparator<String>() {
			@Override
			public int compare(String b1, String b2) {
				return b1.compareTo(b2);
			}
		});

		for (String i : b) {
			sb.append(String.format("%s/%s/%s/%s/%s%n", this.books.get(i).getBookNo(), this.books.get(i).getBookTitle(),
					this.books.get(i).getPublisher(), this.books.get(i).getAuthor(),
					this.books.get(i).getBookStatus()));

		}
		sb.append(String.format("----------------------------------------------%n"));
		return sb.toString();

	}

	
	// 전체 사용자 출력
	public String viewAllUsers() {
		System.out.println();
		System.out.println("전체 회원 목록 입니다.");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("--------------------------------------------------%n"));
		sb.append(String.format("회원번호/아이디/이름/전화번호%n"));
		sb.append(String.format("--------------------------------------------------%n"));

		Set<String> key = this.users.keySet();

		// USER의 KEY값을 저장하기 위한 임시변수 LIST b
		List<String> b = new ArrayList<String>(key);

		Collections.sort(b, new Comparator<String>() {
			@Override
			public int compare(String b1, String b2) {
				return b1.compareTo(b2);
			}
		});

		for (String i : b) {

			sb.append(String.format("%s/%s/%s/%s%n", this.users.get(i).getUserNo(), this.users.get(i).getUserId(),
					this.users.get(i).getName(), this.users.get(i).getphone()));
		}
		sb.append(String.format("--------------------------------------------------%n"));
		return sb.toString();
	}

	// 연체 도서 전체 보기
	public String viewOverdueBooks() {
		/*
		 * 등록번호 - book <> checkout 도서명 - book 저자 - book 출판사 -book 대출일 - checkout 반납예정일 -
		 * checkout 연체일수 - checkout 대출인 - user 회원번호 - user <> checkout
		 */
		// 연체일 계산 메소드 호출
		this.setAllOverdueDays();
		// BookNO와 UserNo를 저장하기 위한 임시변수

		StringBuilder sb = new StringBuilder();
		int a = 0;
		// 퍼센트 계산용 books 사이즈 변수
		double per = this.books.size();

		if (this.checkOuts.size() != 0) {

			sb.append(String.format("오늘 날짜 : %s%n", this.nowDate));

			sb.append(String.format("----------------------------------------------------------------------%n"));
			sb.append(String.format("등록번호/도서명/저자/출판사/대출일/반납예정일/연체일수/대출인/회원번호%n"));
			sb.append(String.format("----------------------------------------------------------------------%n"));

			Collections.sort(this.checkOuts);

			for (CheckOut c : checkOuts) {
				Book book = this.books.get(c.getBookNo());
				if (book.getBookStatus().equals("연체중") && (c.getReturnDate() == null || c.getReturnDate().equals(""))) {
					++a;
					User user = this.users.get(c.getUserNo());
					sb.append(String.format("%s/%s/%s/%s/%s/%s/%s/%s/%s%n", book.getBookNo(), book.getBookTitle(),
							book.getAuthor(), book.getPublisher(), c.getCheckOutDate(), c.getDueDate(),
							c.getOverdueDays(), user.getName(), c.getUserNo()));
				}
			}
		}
		if (a <= 0) {
			sb.append(String.format("연체 중인 도서 목록이 없습니다.%n"));
		} else {
			double e = (a / per) * 100.0;
			sb.insert(0, String.format("%n도서관내[%d%%]책이 연체중 입니다.%n", (int) e));
			sb.append(String.format("----------------------------------------------------------------------%n"));
		}
		return sb.toString();
	}

	// 연체일수 계산 메소드
	public void setAllOverdueDays() {
		int a = 0;
		LocalDate nowDate = LocalDate.now();
		for (CheckOut c : this.checkOuts) {
			if ((c.getReturnDate() == null || c.getReturnDate().equals(""))
					&& nowDate.isAfter(LocalDate.parse(c.getDueDate()))) {
				Period period = Period.between(nowDate, LocalDate.parse(c.getDueDate()));
				a = period.getDays();
				c.setOverdueDays(a);

			}
		}
	}

	// 관리자 - 메세지를 보낸다.
	public void sendMessages(String message) {

		for (CheckOut c : this.checkOuts) {
			if (c.getOverdueDays() != 0) {
				this.users.get(c.getUserNo()).setMessages(message);
			}
		}
	}

	// 현재 사용자의 대출 가능한지 구하기(3권 초과 불과)
	public int isCheckOutAble() {
		//결과를 반환할 변수 선언
		int result = 0;
		//도서 대출 정보가 들어있는 컬렉션에서 반복
		for (CheckOut c : this.checkOuts) {
			//반납되지 않았으면서(null)->이전 대출 이력을 반환하지 않기 위해 필요, 대출 중인 책이 현재 사용자의 고유번호와 같다면   
			if (c.getReturnDate() == null && c.getUserNo().equals(utils.getCurrentUser().getUserNo())) {
				//도서를 대출 중이므로 대출 중인 수량 1씩 더해주기
				++result;
			}
		}
		//대출 중인 수량 결과값 반환
		return result;
	}

	// 현재 책이 비치중인지 구하기
	//@Param 도서의 고유번호를 받아와서 결과값 반환
	public boolean isCheckOutAble2(String bookNo) {
		//책이 비치중인지 반환할 변수 선언
		boolean result = false;
		//해당 도서의 상태가 비치중이라면
		if (this.books.get(bookNo).getBookStatus().equals("비치중")) {
			//대출 가능하므로 결과값에 true 할당 
			result = true;
		}
		//대출 가능 여부 결과값 반환
		return result;
	}
}
