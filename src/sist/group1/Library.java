package sist.group1;

import java.util.Scanner;

public class Library {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		LibraryService service = new LibraryService();
		try {
			while (true) {
				System.out.println();
				System.out.println(
						"=======================================================================================");
				System.out.println("　　　　　　　　　　　　　　　　　　쌍용 도서관");
				System.out.println(
						"=======================================================================================");
				System.out.println("**************로그인이 필요합니다. 회원가입 또는 로그인을 진행 해주세요.**************");
				System.out.println();

				System.out.println(
						"=======================================================================================");
				System.out.println("1.회원가입   2.로그인   0.종료");
				System.out.println(
						"=======================================================================================");
				System.out.print("선택 > ");
				int input = sc.nextInt();
				sc.nextLine();
				if (input == 0)
					break;
				switch (input) {
				case 1:
					service.register(sc);
					break;
				case 2:
					service.login(sc);
					break;
				default:
					System.out.println("알 수 없는 입력입니다. 다시 시도하세요.");
					break;
				}
			}
			sc.close();
			service.fileSave();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
