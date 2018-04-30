쌍용 교육센터 Java&Python 기반 응용SW개발자 양성과정 (기간 : 2017년 11년 1일 ~ 2018년 6월 1일)

# 도서 대출 관리 프로그램

* 기간 : 2017/12/18 ~ 2018/01/05
* 환경 : 콘솔 기반
* 주제 : 도서 대출 관리
* 기술 : JavaSE , OOP, Collection, Generic, File IO, Serialization
* 진행절차 : 요구분석 → 화면 설계 → 자료구조 설계 → 액션 설계 → 액션 구현 → 테스트 → 최종실행
* 프로젝트의 디테일한 진행 과정은 [여기](https://youmekko.github.io/categories/TeamProject/) 


### 프로젝트 개요

도서의 입고 및 출고 등 관리 기능과 사용자 입장에서의 대여 및 반납 기능을 상호 호환한 프로그램

##### 관리자

* 책을 등록, 삭제할 수 있으며, 전체 도서의 대출 내역을 확인할 수 있습니다.
* 현재 대출중인 도서 리스트에서 도서의 대출일/ 반납일 / 반납 예정일을 수정할 수 있습니다.
* 현재 연체중인 도서 리스트에서 연체중인사용자에게 연체 안내 메시지를 발송할 수 있습니다.
* 회원 관리에서 모든 유저의 도서 대출 내역을 확인 할 수 있습니다.

##### 사용자

*  도서를 검색하고 대출 할 수 있습니다.
*  개인별 대출현황(대출중 / 연체중 / 과거 대출내역)을 확인하고 도서를 반납할 수 있습니다.
*  관리자로부터의 메시지를 확인할 수 있습니다.

### 구현 화면
![](/result.gif)

### 참여 (사용자의 도서대출, 반납, 대출/반납 목록 보기 구현)

* 액션플로우

  1. 사용자가 전체도서 목록이나, 검색에서 찾은 도서 목록에서 도서 등록번호를 입력해, 도서를 대출 한다. 
  2. 사용자가 현재 자신이 대출중인 도서의 목록을 본다.
  3. 대출중 목록에서 도서 등록번호를 입력해 도서를 반납한다. 
  4.  반납 완료한 도서 목록을 볼 수 있다. 

* 코드 

  1. DAO 필드 선언

  ~~~java
  //checkOut 객체는 userNo, bookNo를 필드값으로 가지고 있다. 
  //users, books는 Map컬렉션으로 각 userNo, bookNo이 key이다.  
  private Map<String, User> users = new HashMap<String, User>();
  private Map<String, Book> books = new HashMap<String, Book>();
  private List<CheckOut> checkOuts = new ArrayList<CheckOut>();

  //오늘 날짜를 "yyy-MM-DD" 패턴으로 얻기 위한 필드
  private LocalDate now = LocalDate.now();
  private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private String nowDate = this.now.format(dateFormat);
  ~~~

  2. LocalDate의 now메소드를 활용해 오늘 날짜와, 오늘 날짜로 부터 7일 이후의 날짜(반납예정일)를 (yyyy-MM-dd) 형식으로 얻기 위한 메소드

  ~~~java
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
  ~~~

  3. 사용자가 도서를 대출하기 위한 메소드

  ~~~java
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
  ~~~

  4. 사용자가 대출중인 도서 목록을 보기 위한 메소드

  ~~~java
  // viewCheckedOutBooks 사용자가 대출중인 도서 목록을 본다.
  public String viewUserCheckedOutBooks() {
       this.setAllOverdueDays();
  	StringBuilder sb = new StringBuilder();
  	sb.append("\n");
  	sb.append(String.format("[%s/%s]님의 현재 대출 목록 입니다.%n", this.utils.getCurrentUser().getUserNo(),
  				this.utils.getCurrentUser().getName()));
  	sb.append(String.format("오늘 날짜 : %s%n", this.getToday()));

  	sb.append("---------------------------------------------------------------------------------------\n");
  	sb.append(String.format("등록번호/도서명/대출일/반납예정일/대출현황/연체일수%n"));
  	sb.append("---------------------------------------------------------------------------------------\n");

  	// 체크아웃 리스트가 가진 모든 체크아웃 객체들 중에
  	for (CheckOut checkOut : this.checkOuts) {
  		// 현재 회원번호와 체크아웃 객체가 가진 회원번호가 같을 때
  		if (checkOut.getUserNo().equals(this.utils.getCurrentUser().getUserNo())) {
  			// 해당 체크아웃 객체가 가진 도서 등록번호만을 가지고 와서 book 변수에 담고
  			Book book = books.get(checkOut.getBookNo());
  			// 해당 북이 대출중이거나 연체중이라면
  			if (book.getBookStatus().equals("대출중") || book.getBookStatus().equals("연체중")) {
  			// StringBuilder에 내용을 붙여준다.
  			sb.append(String.format("%s/%s/%s/%s/%s/%d%n", book.getBookNo(), book.getBookTitle(),checkOut.getCheckOutDate(), checkOut.getDueDate(), book.getBookStatus(),checkOut.getOverdueDays()));
  		    }
          }

      }
  		sb.append("---------------------------------------------------------------------------------------");
  		return sb.toString();
  }
  ~~~

  5. 도서를 반납하기 위한 메소드

  ~~~java
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
  ~~~

  6. 사용자가 반납한 도서 목록을 보기 위한 메소드

  ~~~java
  // viewReturnedBooks 사용자가 반납한 도서 목록을 본다.
  public String viewReturnedBooks() {
  	StringBuilder sb = new StringBuilder();
  	sb.append("\n");
  	sb.append(String.format("[%s/%s]님의 반납 목록 입니다.%n", this.utils.getCurrentUser().getUserNo(),
  				this.utils.getCurrentUser().getName()));
  	sb.append(String.format("오늘 날짜 : %s%n", this.getToday()));
  	sb.append("---------------------------------------------------------------------------------------\n");
  	sb.append(String.format("회차/등록번호/도서명/대출일/반납일/연체일수%n"));
  	sb.append("---------------------------------------------------------------------------------------\n");
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
  	sb.append("---------------------------------------------------------------------------------------\n");
  	return sb.toString();
  }
  ~~~

  ​

### 화면 흐름

![](/flow.png)

### Class 다이어그램

![](/class.png)