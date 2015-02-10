package uncc.goldrush.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.Clinic;
import uncc.goldrush.bean.Owner;
import uncc.goldrush.bean.User;
import uncc.goldrush.dao.AccountMapper;
import uncc.goldrush.dao.AccountMapperImpl;
import uncc.goldrush.util.Assert;
import uncc.goldrush.util.DBUtil;

/**
 * Servlet implementation class AccountsServlet
 */
public class AccountsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(AccountsServlet.class);
	Clinic clinic = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AccountsServlet() {
		super();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
					logger.trace("Entering doGet()");        
					String accountName = request.getParameter("AccountName");
					int newBalance = Integer.valueOf(request.getParameter("NewBalance"));
					
					if (request.getSession().getAttribute("USER") == null) {
						logger.warn("User not authenticated");
						response.sendRedirect(request.getContextPath() + "/login.jsp");
					} else if (((User) request.getSession().getAttribute("USER"))
							.ownAccount(accountName)) {
						SqlSession session = null;
						try {
							session = DBUtil.getSqlMapper().openSession();
							AccountMapperImpl accountMapperImpl = (AccountMapperImpl) session
									.getMapper(AccountMapper.class);
							logger.info("Updating account information");
							accountMapperImpl.updateAccount(accountName, newBalance);
						} catch(Exception e) {
							System.err.println("Account hasn't been properly updated!");
							e.printStackTrace();
						} finally{
							if (session != null) {
							try {
								session.close();
							} catch (Exception ex) {
							}
						}
					}
    }
	}
}
