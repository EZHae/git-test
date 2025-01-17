package com.itwill.myblog.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itwill.myblog.domain.Member;
import com.itwill.myblog.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MemberSignUpController
 */
@WebServlet(name = "memberSignUpController", urlPatterns = { "/member/signup" })
public class MemberSignUpController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MemberSignUpController.class);
	private final MemberService memberService = MemberService.INSTANCE;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MemberSignUpController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/member/signup.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 양식 데이터(username, password, email)의 값들을 읽음
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		log.debug("doPost(username={}, password={}, email={})", username, password, email);
		
		// 서비스 계층의 메서드를 호출해서 DB members 테이블에 회원정보를 삽입
		Member member = Member.builder().userName(username).password(password).email(email).build();
		memberService.signUp(member);
		
		// 목록 페이지 이동
		String url = request.getContextPath() + "/member/signin";
		response.sendRedirect(url);
	}
}