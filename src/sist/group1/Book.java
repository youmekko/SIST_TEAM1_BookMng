package sist.group1;

import java.io.Serializable;

public class Book implements Serializable, Comparable<Book> {

	/**
	 * 직렬화 UID
	 */
	private static final long serialVersionUID = 2L;
	/*
	 * @Param 고유번호(ex. BOO1, BOO2...), 도서명, 저자, 출판사, 현재 도서 상태
	 */
	private String bookNo;
	private String bookTitle;
	private String author;
	private String publisher;
	private String bookStatus; // 비치중, 대출중, 연체중

	public Book() {

	}

	public Book(String bookNo, String bookTitle, String author, String publisher) {

		this.bookNo = bookNo;
		this.bookTitle = bookTitle;
		this.author = author;
		this.publisher = publisher;
		setBookStatus("비치중");
	}

	public String getPublisher() {
		return publisher;
	}

	public String getBookNo() {
		return bookNo;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public String getAuthor() {
		return author;
	}

	public String getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(String bookStatus) {
		this.bookStatus = bookStatus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-10s%-20s%-20s%-10s%-10s%n", this.bookNo, this.bookTitle, this.author, this.publisher,
				this.bookStatus));
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Book) {
			Book book = (Book) obj;
			if (this.bookNo.equals(book.getBookNo())) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return 31 * this.bookNo.hashCode();
	}

	@Override
	public int compareTo(Book book) {
		return this.bookNo.compareTo(book.getBookNo());
	}
}
