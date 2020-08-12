package board;

/* 
 * cmd
 * CREATE TABLE board(num int not null auto_increment primary key, subject varchar(30) not null,
 * contents text not null, writer varchar(6) not null, date datetime , views int default 1);
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;


public class BoardDao {
	
	//전역변수
	Board b = new Board();
	Scanner s = new Scanner(System.in);
	
	//DB접속준비
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	public BoardDao() throws Exception {
		run();
	}
	
	public void run() throws Exception {

		while(true) {
			
			System.out.println("=========================================================================================");
			System.out.println("                                    DB이용한 게시판 만들기                                                 ");
			System.out.println("-----------------------------------------------------------------------------------------");
			System.out.println("1.게시판 목록확인 | 2.게시글 상세보기 | 3.게시글 등록 | 4.게시글 수정 | 5.게시글 삭제 | 6.종료 ");
			System.out.println("=========================================================================================");
			System.out.println();
			
			System.out.print("선택>> ");
			int choiceNum = s.nextInt();
			s.nextLine();
			
			switch(choiceNum) {
			case 1: //게시판목록확인
				selectBoard();
				break;
				
			case 2: //게시글 상세보기
				System.out.print("상세보기 할 게시글의 번호 입력 > ");
				viewMore(s.nextInt());
				plusViews();
				s.nextLine();
				break;
				
			case 3: //게시글 등록
				Input();
				InsertBoard();
				break;
				
			case 4: //게시글 수정
				updateBoard();
				s.nextLine();
				break;
			
			case 5: //게시글 삭제
				System.out.print("삭제할 게시글의 번호 입력 > ");
				deleteBoard(s.nextInt());
				s.nextLine();
				break;
				
			default : //종료
				System.out.println();
				System.out.println("[프로그램을 종료합니다.]");
				System.exit(0);
			}
		}
		
	}
	
	public Connection getConnection() throws Exception { //db 연결객체  //상수는 원래 대문자로 다적는다
 		
		String DRIVER = "com.mysql.cj.jdbc.Driver";
 		String URL 	  = "jdbc:mysql://localhost:3306/jdbc?serverTimezone=UTC";
 		String ID	  = "root";
 		String PWD    = "root";
	
 		try {
 			Class.forName(DRIVER);
 			conn = DriverManager.getConnection(URL,ID,PWD);
 			//System.out.println("디비 연결 성공"); //테스트 이후 주석처리
 			
 		}catch(SQLException e) {
 			e.printStackTrace();
 		}
 		
 		return conn;
 		
	}
	
	public void selectBoard() throws Exception {
		
		Vector<Board> v = new Vector<Board>();
		StringBuffer sb = new StringBuffer();
		
		//SELECT * FROM board ORDER BY num;
		sb.append("SELECT * FROM board ORDER BY num");
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				b = new Board();
				b.setNum( rs.getInt("num") );
				b.setSubject( rs.getString("subject") );
				b.setContents( rs.getString("contents") );
				b.setWriter( rs.getString("writer") );
				b.setDate(rs.getDate("date"));
				b.setViews(rs.getInt("views"));
				
				v.add(b);
			}
			
			if( v.size() == 0 ) {
				System.out.println("[게시글이 없습니다.]");
				System.out.println();
			}else {
				System.out.println();
				System.out.println("[ 전체 게시물 수 : " + v.size() + " ]");
				for(int i = 0 ; i < v.size() ; i++) {
					System.out.println("-------------------------------------------------------");
					System.out.println( v.get(i).toStringList() );
					System.out.println("-------------------------------------------------------");
				}
			}
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally {
			rs.close();
			pstmt.close();
			conn.close();
		}
		
	}
	
	public void viewMore(int viewNum) throws Exception {

		ArrayList<Board> arr = new ArrayList<Board>();
		
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer();
			//SELECT num,subject,contents,writer,date,views FROM board WHERE num = ?
			sb.append("SELECT num,subject,contents,writer,date,views,passwd FROM board ");
			sb.append("WHERE num = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1,viewNum);
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				b.setNum( rs.getInt("num") );
				b.setSubject( rs.getString("subject") );
				b.setContents( rs.getString("contents") );
				b.setWriter( rs.getString("writer") );
				b.setDate(rs.getDate("date"));
				b.setViews(rs.getInt("views"));
				b.setPasswd(rs.getString("passwd"));
				
				arr.add(b);
			}
			
			if( arr.size() == 0 ) {
				System.out.println("[해당 순번의 게시글이 없습니다.]");
				System.out.println();
			}else {
				for(int i = 0 ; i < arr.size() ; i++) {
					System.out.println();
					System.out.println(arr.get(i).toString());
					System.out.println();
				}
			}
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally {
			rs.close();
			pstmt.close();
			conn.close();
		}
		
	}
	
	public void plusViews() throws Exception { //조회수 업데이트
		
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer();
			
			//UPDATE board SET views=? WHERE num=?
			int views = b.getViews();
			views++ ;
			b.setViews(views);
			
			sb.append("UPDATE board SET views = ? WHERE num = ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, b.getViews());
			pstmt.setInt(2, b.getNum());
			
			pstmt.executeUpdate();
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally {
			pstmt.close(); 
			conn.close();
		}
	}
	
	public void Input() {
		
		System.out.print("게시글 제목 입력 > ");
		b.setSubject(s.next());
		
		System.out.print("게시글 내용 입력 > ");
		b.setContents(s.next());
		
		System.out.print("작성자 입력 > ");
		b.setWriter(s.next());
		
		b.setViews(1);
		
		System.out.print("비밀번호 입력 > ");
		b.setPasswd(s.next());
		
		System.out.println();
		
	}
	
	
	public void InsertBoard() throws Exception {
		
		StringBuffer sb = new StringBuffer();
		/* INSERT INTO board(subject,contents,writer,date,views,passwd) VALUES(?,?,?,now(),?,?) */
		
		sb.append( "INSERT INTO board(subject,contents,writer,date,views,passwd)" );
		sb.append( " VALUES(?,?,?,now(),?,?)" );
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sb.toString());//연결한 디비에 prep해서 사용
			
			pstmt.setString(1,b.getSubject());
			pstmt.setString(2,b.getContents());
			pstmt.setString(3,b.getWriter());
			pstmt.setInt(4,b.getViews());
			pstmt.setString(5, b.getPasswd());
			
			pstmt.executeUpdate();
			System.out.println("[게시글 작성이 완료 되었습니다.]");
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally { //수정,연결만 쓰니깐 rs(결과)는뺌
			pstmt.close(); 
			conn.close();
		}
		
	}
	
	public void updateBoard() throws Exception {
		
		try {
			System.out.print("수정할 게시글의 번호 입력 > ");
			b.setNum(s.nextInt());
			
			System.out.print("게시글 제목 입력 >");
			b.setSubject(s.next());
			
			System.out.print("게시글 내용 입력 > ");
			b.setContents(s.next());
			
			System.out.print("작성자 입력 > ");
			b.setWriter(s.next());
			
			conn = getConnection();
			StringBuffer sb = new StringBuffer();
			
			//UPDATE board SET subject=?,contents=?,writer=?,date=now() WHERE num = ?
			sb.append("UPDATE board SET subject=?,contents=?,writer=?,date=now() WHERE num = ? ");
		
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, b.getSubject());
			pstmt.setString(2, b.getContents());
			pstmt.setString(3, b.getWriter());
			pstmt.setInt(4, b.getNum());
			
			pstmt.executeUpdate();
			System.out.println( "[" + b.getNum() + "번의 게시글이 수정되었습니다.]");
			System.out.println();
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally { //수정,연결만 쓰니깐 rs는 뺌
			pstmt.close();
			conn.close();
		}
	}
	
	public void deleteBoard(int deleteNum) throws Exception {
		
		try {
			conn = getConnection();
			StringBuffer sb = new StringBuffer();
			//DELETE FROM board WHERE num = ?
			sb.append("SELECT * FROM board WHERE num = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, deleteNum);
			rs = pstmt.executeQuery();
			
			while( rs.next() ) { //가져올 결과가 있으면
				
				int result = rs.getInt(1);

				if( result > 0 ) {
					
					System.out.print("비밀번호를 입력하세요 > ");
					String isPasswd = s.next();
					String realPasswd = rs.getString("passwd");
					
					if( realPasswd.equals(isPasswd) ) {
					
						String sql = "DELETE FROM board WHERE num = ? ";
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, deleteNum);
						pstmt.executeUpdate();
						
						System.out.println("[" + deleteNum + "번 게시글을 삭제하였습니다.]");
						System.out.println();
						
					}else {
						System.out.println();
						System.out.println("[비밀번호가 틀렸습니다!]");
						System.out.println();
						return;
					}
					
				}
			}

			System.out.println();
			System.out.println("[해당 게시글이 존재하지 않습니다.]");
			System.out.println();
			
			
		}catch(SQLException sqle) {
			sqle.printStackTrace();
			
		}finally {
			rs.close();
			pstmt.close();
			conn.close();
		}
	}

}

