package board;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Board {
	private int num; //순번
	private String subject; //제목
	private String contents; //내용
	private String writer; //작성자
	private Date date; ;//등록일
	private int views; //조회수
	private String passwd; //비밀번호
	
	public Board() {
		
	}
	
	//getset
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}
	
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	//날짜포맷 : 년-월-일-시:분:초 -> 20/8/4
	SimpleDateFormat sdf = new SimpleDateFormat("yy-M-d");

	@Override
	public String toString() { //상세보기용
		return  "순번 : " + getNum() + 
				"\n제목 : " + getSubject() +
				"\n내용 : " + getContents() +
				"\n작성자 : " + getWriter() +
				"\n등록일 : " + sdf.format(getDate()) +
				"\n조회수 : " + getViews() +
				"\n비밀번호 : " + getPasswd();
	}
	
	public String toStringList() { //목록UI용
		return "순번 : " + getNum() + 
				"\t제목 : " + getSubject() +
				"\t작성자 : " + getWriter() +
				"\t등록일 : " + sdf.format(getDate());
		
	}
	
}

