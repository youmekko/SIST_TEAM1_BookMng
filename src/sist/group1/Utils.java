package sist.group1;

public class Utils {

	/*
	 * @Param 관리자 ID,PW -> admin123 Singleton -> 인스턴스로 Utils utils =
	 * Utils.getInstance(); 형식으로 가져오면 됩니다. 클래스 내의 아무 곳에서나 utils.메소드명(); 으로 값 핸들링 가능
	 */
	// 관리자 아이디, 비밀번호
	private final String ADMIN = "admin";
	// Utils static 객체 생성
	private static Utils utils = null;
	// 현재 사용자 정보 저장용 변수 선언
	private User user;

	private Utils() {

	}

	public static Utils getInstance() {
		if (utils == null) {
			utils = new Utils();
		}
		return Utils.utils;
	}

	public String getAdmin() {
		return ADMIN;
	}

	public void setCurrentUser(User user) {
		this.user = user;
	}

	public User getCurrentUser() {
		return user;
	}

}
