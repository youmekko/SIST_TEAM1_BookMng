package sist.group1;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LibraryService {
	
	//도서 관리 DAO 객체 생성
	private LibraryDAO dao = new LibraryDAO();
	//관리자 정보, 현재 사용자 정보를 가진 Singleton 객체 생성
	private Utils utils = Utils.getInstance();

	//로그인 메소드 선언
	//@Param 사용자 입력을 받기 위해 스캐너 받아오기
	public void login(Scanner sc) {
		//아이디, 비밀번호 입력 요청
		System.out.println();
		System.out.println("로그인을 진행합니다.");
		System.out.print("아이디> ");
		String userId = sc.next();
		System.out.print("비밀번호> ");
		String password = sc.next();
		//관리자 id 및 비밀번호를 입력 했을 경우라면
		if (userId.equals(utils.getAdmin()) && password.equals(utils.getAdmin())) {
			System.out.println("관리자로 로그인 했습니다.");
			//관리자 메뉴 보여주기
			this.adminMenu(sc);
		} else {
			try {
				//사용자 정보가 있는지 비교 후, 정의된 Exception 처리 (등록되지 않은 사용자 입니다. 다시 입력해주세요., 잘못된 비밀번호 입니다. 다시 입력해주세요.)
				this.isWrongUser(userId, password);
				//사용자 정보가 없지 않다면(!null)
				if (this.dao.getCurrentUser(userId, password) != null) {
					//현재 사용자 객체 정보 저장
					utils.setCurrentUser(this.dao.getCurrentUser(userId, password));
					//사용자 메뉴 보여주기
					this.userMenu(sc);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/* 관리자 메뉴 */
	private void adminMenu(Scanner sc) {
		int input = 0;
		while (true) {
			try {
				System.out.println();
				System.out.println("1.도서 관리   2.회원 관리   0.로그 아웃");
				System.out.print("선택> ");
				input = sc.nextInt();
				sc.nextLine();
				if (input == 0)
					break;
				switch (input) {
				/* 도서 관리 메뉴 메소드 호출 */
				case 1:
					this.adminMenuSub1(sc);
					break;
				/* 회원 관리 메뉴 메소드 호출 */
				case 2:
					this.adminMenuSub2(sc);
					break;
				default:
					System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
				}

			} catch (Exception e) {
				System.out.println("잘못된 입력입니다. 다시 입력해 주세요.");
				sc.nextLine();
			}
		}
	}

	/* 관리자 메뉴 서브1 도서관리 */
	private void adminMenuSub1(Scanner sc) {
		while (true) {
			System.out.println();
			System.out.println("1.전체 도서   2.도서 검색   3.대출중 도서   4.연체중 도서   0.나가기");
			System.out.print("선택> ");
			int input = sc.nextInt();
			sc.nextLine();
			if (input == 0)
				break;
			switch (input) {
			case 1:
				System.out.println(this.dao.viewAllBooks());
				this.adminSearchSub(sc);
				break;
			case 2:
				this.searchForBooks(sc);
				break;
			case 3:
				System.out.println(this.dao.viewCheckedOutBooks());
				if (this.dao.viewCheckedOutBooks().equals("대출 중인 책이 없습니다.")) {
					break;
				} else {
					this.checkedOutBooks(sc);
				}
				break;
			case 4:
				System.out.println(this.dao.viewOverdueBooks());
				if (this.dao.viewOverdueBooks().equals("연체 중인 도서 목록이 없습니다.")) {
					break;
				} else {
					this.overdueSub(sc);
				}
				break;
			default:
				System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
			}
		}

	}

	/* 관리자 도서 검색 */
	private void adminSearchSub(Scanner sc) {

		System.out.println();
		System.out.println("1.도서 등록   2.도서 삭제   0.나가기");
		System.out.print("선택> ");
		int input = sc.nextInt();
		sc.nextLine();
		switch (input) {
		case 0:
			break;
		case 1:
			this.registerBook(sc);
			break;
		case 2:
			this.deleteBook(sc);
			break;
		}
	}

	/* 관리자 메뉴 서브2 회원관리 */
	private void adminMenuSub2(Scanner sc) {
		while (true) {
			System.out.println();
			System.out.println("1.전체 회원   2.회원 검색  0.나가기");
			System.out.print("선택> ");
			int input = sc.nextInt();
			sc.nextLine();
			if (input == 0)
				break;
			switch (input) {
			case 1:
				System.out.println(this.dao.viewAllUsers());
				this.viewUserInDetail(sc);
				break;
			case 2:
				this.serachForUsers(sc);
				break;
			default:
				System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
			}
		}
	}

	/* 사용자 초기 메뉴 */
	private void userMenu(Scanner sc) {
		/* 관리자에게 온 메세지가 [1]개 있습니다. (메세지 메소드 호출) */
		/* [1] 연체중인 도서가 있습니다. 반납 해주세요. (연체 메소드 호출) */
		System.out.println();
		System.out.printf("[%s]님으로 로그인 했습니다.%n%n", utils.getCurrentUser().getName());
		System.out.println(this.dao.viewAllMessages());
		while (true) {

			try {
				System.out.println();
				System.out.println("1.도서 검색   2.대출 목록   3.반납 목록   4.메시지 확인   0.로그아웃");
				System.out.print("선택> ");
				int input = sc.nextInt();
				if (input == 0)
					break;
				switch (input) {
				/* 도서 검색 sub메뉴 */
				case 1:
					this.searchForBooksSub(sc);
					break;
				/* 대출 목록 sub메뉴 */
				case 2:
					this.viewCheckedOutBooks(sc);
					break;
				/* 반납 목록 sub메뉴 */
				case 3:
					System.out.println(this.dao.viewReturnedBooks());
					break;
				/* 메세지 확인 sub메뉴 */
				case 4:
					this.viewAllMessages(sc);
					break;
				default:
					System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
				}
			}

			catch (Exception e) {
				System.out.println("잘못된 입력입니다. 다시 입력해 주세요.");
				sc.nextLine();
			}

		}

	}

	//사용자 회원가입 메소드 선언
	//@Param 사용자 입력을 받기 위해 스캐너 받아오기
	public void register(Scanner sc) {
		System.out.println();
		System.out.println("회원가입을 진행합니다.");

		System.out.print("아이디> ");
		String userId = sc.next();
		System.out.print("비밀번호(영문+숫자 조합 8자 이상)> ");
		String password = sc.next();
		while (true) {
			try {
				//비밀번호 패턴 예외처리
				this.isPasswordPattern(password);
				break;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.print("비밀번호(영문+숫자 조합 8자 이상)> ");
				password = sc.next();
			}
		}
		System.out.print("이름> ");
		String name = sc.next();
		System.out.print("전화번호(010-1234-5678)> ");
		String phone = sc.next();
		while (true) {
			try {
				//전화번호 패턴 예외처리
				this.isPhonePattern(phone);
				break;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.print("전화번호(010-1234-5678)> ");
				phone = sc.next();
			}
		}
		//사용자 정보가 이미 존재하거나 관리자용 아이디를 입력한경우라면
		if (this.dao.getCurrentUser(userId, password) != null || userId.equals(utils.getAdmin())) {
			//회원 가입 불가
			System.out.println("이미 존재하는 id 입니다. 다시 입력해주세요.");
		} else {
			//회원 가입 메소드 호출
			this.dao.register(userId, password, name, phone);
			System.out.println("회원 가입이 완료 되었습니다.\n");
		}
	}

	// 도서 등록
	private void registerBook(Scanner sc) {
		System.out.println();
		System.out.println("등록할 도서를 입력해주세요.");
		System.out.print("도서명> ");
		String bookTitle = sc.next();
		System.out.print("출판사> ");
		String publisher = sc.next();
		System.out.print("저자> ");
		String author = sc.next();
		//도서 등록 메소드 호출
		String bookNo = this.dao.registerBook(bookTitle, author, publisher);
		//도서 등록 보여주기
		System.out.printf("[%s/%s]이 등록되었습니다.%n", bookNo, bookTitle);
	}

	// 프로그램 종료시 users, books, checkOuts 데이터 저장용 메소드 선언
	public void fileSave() {
		//프로그램 종료 메세지 보여주기
		System.out.println();
		System.out.println("<<<쌍용 도서관>>> 프로그램을 종료합니다..");
		//파일 저장 메소드 호출
		this.dao.logout();
	}

	/* 회원 가입시 예외처리 */
	private void isPhonePattern(String password) throws PatternException {
		//전화번호 패턴 (010-1234-5678)
		String temp = "(\\d{3}).*(\\d{3}).*(\\d{4})";
		Boolean bool = Pattern.matches(temp, password);
		if (!bool) {
			throw new PatternException("잘못된 전화번호 형식 입니다. 다시 입력해주세요.");
		}
	}

	private void isPasswordPattern(String phone) throws PatternException {
		//비밀번호 패턴 (영문 소문자 + 숫자 혼용 8글자~15글자)  
		String temp = "(?=.*\\d)(?=.*[a-z]).{8,15}";
		Boolean bool = Pattern.matches(temp, phone);
		if (!bool) {
			throw new PatternException("잘못된 비밀번호 형식 입니다. 다시 입력해주세요.");
		}
	}

	private void isWrongUser(String userId, String password) throws ExistUserException {
		//사용자 입력 정보가 없거나 관리자 정보가 아닐 경우라면
		if (this.dao.getCurrentUser(userId, password) == null && !userId.equals(utils.getAdmin())) {
			throw new ExistUserException("등록되지 않은 사용자 입니다. 다시 입력해주세요.\n");
		}
		if (this.dao.isWrongPassword(userId, password)) {
			throw new ExistUserException("잘못된 비밀번호 입니다. 다시 입력해주세요.\n");
		}
	}

	// 연체 중인 도서를 가진 사용자에게 메세지 보내기 서브 메뉴
	private void overdueSub(Scanner sc) {
		while (true) {
			System.out.println();
			System.out.println("1.메세지 전송   0.나가기");
			System.out.print("선택> ");
			int input = sc.nextInt();
			sc.nextLine();
			if (input == 0) {
				break;
			} else if (input == 1) {
				System.out.println();
				System.out.println("연체중인 회원들에게 메세지를 전송합니다.");
				System.out.print("메세지 입력> ");
				String message = sc.nextLine();
				this.dao.sendMessages(message);
				System.out.println("메세지가 전송 되었습니다.");
			}
		}
	}

	// 도서를 검색(관리자 전용)
	private void searchForBooks(Scanner sc) {
		boolean run = true;
		while (run) {
			System.out.println();
			System.out.println("1.등록번호 검색   2.도서명 검색   3.출판사 검색   4.저자 검색   0.나가기");
			System.out.print("선택> ");

			int selectNum = sc.nextInt();
			sc.nextLine();
			// System.out.println("검색할 도서를 입력해주세요");
			switch (selectNum) {
			case 1:
				this.searchForBooks("등록번호", sc);
				break;
			case 2:
				this.searchForBooks("도서명", sc);
				break;
			case 3:
				this.searchForBooks("출판사", sc);
				break;
			case 4:
				this.searchForBooks("저자", sc);
				break;
			case 0:
				run = false;
				break;
			default:
				System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
			}
		}
	}

	// 도서를 대출 (사용자 전용)
	private void checkOutBook(Scanner sc) {
		System.out.println();
		System.out.println("대출할 책의 등록번호을 입력해 주세요.");
		System.out.print("등록번호 입력> ");
		String bookNo = sc.next();
		try {
			this.isExistBook(bookNo);
			if (this.dao.isCheckOutAble() >= 3) {
				System.out.println("3권 이상의 책은 대출 할 수 없습니다.");
			} else if (!this.dao.isCheckOutAble2(bookNo)) {
				System.out.println("현재 비치중인 책만 대출 가능합니다.");
			} else {
				System.out.println(this.dao.checkOutBook(bookNo));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// 도서 대출 서브 메뉴
	private void checkOutBookSub(Scanner sc) {

		System.out.println();
		System.out.println("1.대출 하기   0.나가기");
		System.out.print("선택> ");

		int input = sc.nextInt();
		sc.nextLine();

		switch (input) {
		case 0:
			break;
		case 1:
			this.checkOutBook(sc);
			break;

		}

	}

	// 도서를 검색(사용자 전용)
	private void searchForBooksSub(Scanner sc) {
		while (true) {
			System.out.println();
			System.out.println("1.전체 도서 목록   2.등록번호   3.제목 검색   4.저자 검색   5.출판사 검색   0.나가기");
			System.out.print("선택> ");

			int selectNo = sc.nextInt();
			sc.nextLine();

			if (selectNo == 0)
				break;

			switch (selectNo) {
			case 1:
				System.out.println(this.dao.viewAllBooks());
				this.checkOutBookSub(sc);
				break;
			case 2:
				this.searchForBooks("등록번호", sc);
				this.checkOutBookSub(sc);
				break;
			case 3:
				this.searchForBooks("도서명", sc);
				this.checkOutBookSub(sc);
				break;
			case 4:
				this.searchForBooks("저자", sc);
				this.checkOutBookSub(sc);
				break;
			case 5:
				this.searchForBooks("출판사", sc);
				this.checkOutBookSub(sc);
				break;
			}
		}
	}

	// 도서 검색 메소드 서브
	private void searchForBooks(String key, Scanner sc) {
		System.out.println();
		System.out.println("검색할 도서를 입력해주세요.");
		System.out.printf("%s 입력> ", key);

		String value = sc.nextLine();
		// 검색 진행
		// -> 매개변수 목록에 key, value를 같이 보낸다.
		System.out.println(this.dao.searchForBooks(key, value));
		this.viewBookInDetail(sc);
	}

	// 도서를 삭제
	private void deleteBook(Scanner sc) {

		System.out.println();

		while (true) {

			try {
				System.out.println("삭제할 도서를 선택해주세요.");
				System.out.print("등록번호 입력> ");
				String bookNo = sc.nextLine();
				this.isExistBook(bookNo);
				System.out.print("정말로 삭제하시겠습니까?(Y/N)");
				String input = sc.next();
				if (input.equals("Y")) {
					System.out.println(this.dao.deleteBook(bookNo));
					break;
				} else if (input.equals("N")) {
					break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// 도서 상세보기
	private void viewBookInDetail(Scanner sc) {
		// while문 돌릴때 키값 받는 변수
		// while문 돌릴떄 쓰는 변수
		boolean run = true;
		while (run) {
	
			System.out.println();
			System.out.println("1.도서 상세 보기   0.나가기");
			System.out.print("선택> ");
			int selectNo = sc.nextInt();
			sc.nextLine();
			switch (selectNo) {
			case 1:this.viewBookInDetailSub(sc);break;
			case 0:run = false;
			}
		}
	}

	// 도서 상세보기 서브 메뉴
	private void viewBookInDetailSub(Scanner sc) {
		System.out.println();
		System.out.println("상세보기 할 도서를 선택해주세요.");
		System.out.print("등록번호 입력> ");

		String bookNo = sc.nextLine();

		System.out.println(this.dao.viewBookInDetail(bookNo));
	}

	// 대출중인 도서 목록
	private void viewCheckedOutBooks(Scanner sc) {
		System.out.println(this.dao.viewUserCheckedOutBooks());
		System.out.println();
		System.out.println("1.반납하기   0.나가기");
		System.out.print("선택> ");

		int input = sc.nextInt();
		sc.nextLine();

		switch (input) {
		case 0:
			break;
		case 1:
			this.returnBook(sc);
			break;
		}
	}

	// 도서 반납
	private void returnBook(Scanner sc) {
		System.out.println();
		System.out.println("반납할 도서를 선택해주세요.");
		System.out.print("등록번호 입력> ");
		String bookNo = sc.nextLine();
		System.out.println(this.dao.returnBook(bookNo));
	}

	// 사용자 전체 메세지
	private void viewAllMessages(Scanner sc) {

		if (this.utils.getCurrentUser().getMessages().size() == 0
				|| this.utils.getCurrentUser().getMessages() == null) {
			System.out.println("삭제할 메시지가 없습니다.");
		} else {
			System.out.println();
			System.out.println("1.메세지 한개 삭제   2.메세지 전체 삭제   0.나가기");
			System.out.print("선택> ");
			int input = sc.nextInt();
			sc.nextLine();
			switch (input) {
			case 0:
				break;
			/*
			 * 삭제할 메세지 번호를 입력해주세요. 번호입력> [~번]메세지가 삭제되었습니다.
			 */
			case 1:
				this.viewDeleteOneMessage(sc);
				break;
			case 2:
				this.dao.deleteAllMessages();
				System.out.println("전체 메세지가 삭제되었습니다.");
				break;
			}
		}
	}

	// 사용자 전체 메세지를 삭제
	private void viewDeleteOneMessage(Scanner sc) {

		while (true) {

			System.out.println();
			System.out.println("삭제할 메세지 번호를 입력해주세요.");
			System.out.print("번호입력> ");
			int input = sc.nextInt();
			sc.nextLine();
			try {
				this.dao.deleteOneMessage(input);
				System.out.printf(String.format("%n[%d번]메세지가 삭제되었습니다.%n", input));
				break;
			} catch (Exception e) {
				System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
			}

		}
	}

	// 전체 회원을 검색
	private void serachForUsers(Scanner sc) {
		boolean run = true;
		while (run) {
			System.out.println();
			System.out.println("<회원검색> ");
			System.out.println("1.회원번호 검색   2.이름 검색   3.아이디 검색   4.전화번호 검색   0.나가기");
			System.out.print("선택> ");

			int selectNum = sc.nextInt();
			sc.nextLine();

			System.out.println();
			System.out.println("검색 할 회원을 입력해주세요.");
			String key = null;
			switch (selectNum) {
			case 0:
				run = false;
				break;
			case 1:
				System.out.print("회원번호> ");
				key = sc.next();
				System.out.println(this.dao.serachForUsers("회원번호", key));
				break;
			case 2:
				System.out.print("이름> ");
				key = sc.next();
				System.out.println(this.dao.serachForUsers("이름", key));
				break;
			case 3:
				System.out.print("아이디> ");
				key = sc.next();
				System.out.println(this.dao.serachForUsers("아이디", key));
				break;
			case 4:
				System.out.print("전화번호> ");
				key = sc.next();
				System.out.println(this.dao.serachForUsers("전화번호", key));
				break;
			default:
				System.out.println("알 수 없는 입력입니다. 다시 입력해주세요.");
			}
		}
	}

	// 회원 상세 보기
	private void viewUserInDetail(Scanner sc) {
		boolean run = true;
		while (run) {
			System.out.println();
			System.out.println("1.회원 상세 보기   0.나가기");
			System.out.print("선택> ");

			int selectNo = sc.nextInt();
			sc.nextLine();

			switch (selectNo) {
			// 상세보기 호출
			case 1:
				this.viewUserInDetailSub(sc);
				break;
			case 0:
				run = false;
				break;
			}
		}
	}

	// 회원 상세보기 서브 메뉴
	private void viewUserInDetailSub(Scanner sc) {
		System.out.println();
		System.out.println("상세보기 할 회원 번호를 입력해 주세요");
		System.out.print("회원 번호 입력>");
		String userNo = sc.nextLine();
		System.out.println(this.dao.viewUserInDetail(userNo));
	}

	// 도서 상태에 따른 삭제 및 대출시 예외 처리
	private void isExistBook(String bookNo) throws BookException {
		if (this.dao.getBookStatus(bookNo) == 0) {
			throw new BookException("잘못된 등록번호 입니다. 다시 입력해주세요.\n");
		}
		if (this.dao.getBookStatus(bookNo) == 2) {
			throw new BookException("대출중/연체중 도서는 선택할 수 없습니다.");
		}
	}

	// 반납 예정일 수정
	private void checkedOutBooks(Scanner sc) {

		boolean run = true;

		while (run) {
			System.out.println("1.반납 예정일 수정   0.나가기");
			System.out.print("선택> ");

			int selectNo = sc.nextInt();
			sc.nextLine();

			if (selectNo == 0) {
				run = false;
				break;
			}
			// 수정할 책 번호 및 수정할 날짜 매개변수
			System.out.println();
			System.out.println("반납예정일을 수정할 도서를 선택해주세요.");
			System.out.print("등록번호> ");
			String bookNo = sc.nextLine();
			System.out.print("반납예정일 입력(YYYY-MM-DD)> ");
			String dueDate = sc.nextLine();
			// 반납예정일 형식 예외처리 위한 반복문
			if (this.dueDateExceptionCheck(dueDate)) {
				System.out.println("올바른 형식을 입력하세요.");
			} else {
				System.out.println(this.dao.changeDueDate(bookNo, dueDate));
				break;
			}
		}
	}

	//날짜 형식 예외 처리
	private boolean dueDateExceptionCheck(String dueDate) {
		boolean isOK = false;
		try {
			LocalDate.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			isOK = true;
		}
		return isOK;
	}

}
