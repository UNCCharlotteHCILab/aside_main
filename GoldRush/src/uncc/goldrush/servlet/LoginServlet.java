package uncc.goldrush.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uncc.goldrush.bean.User;
import uncc.goldrush.dao.AccountMapper;
import uncc.goldrush.util.DBUtil;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory
      .getLogger(LoginServlet.class);

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.trace("Entering doPost");

    response.setDateHeader("Expires", 0);
    response.setHeader("Cache-control", "no-store");
    response.setHeader("Pragma", "no-cache");

    SqlSession session = null;
    try {
      logger.debug("Getting sql mapper");
      session = DBUtil.getSqlMapper().openSession();
      AccountMapper accountMapper = session.getMapper(AccountMapper.class);
      User loginUser = new User();
      loginUser.setUsername(request.getParameter("username"));
      
      loginUser.setPassword(request.getParameter("password"));

      logger.info("Getting user with username {}", loginUser.getUsername());
      User currentUser = accountMapper.loginUser(loginUser);
      if (currentUser == null) {
        logger.warn("Invalid username or password");
        request.setAttribute("MESSAGE", "Invalid username or password");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
      } else {
        logger.info("Login successful");

        /* Give the user a brand new session */
        logger.info("Old session id {}", request.getSession().getId());
        request.getSession().invalidate();

        logger.info("New session id {}", request.getSession().getId());
        request.getSession(true).setAttribute("USER", currentUser);
       // request.setAttribute("MESSAGE", "Successfully logged in " + currentUser.getUsername());   
        /* Added as a more obvious example for output validation */
        PrintWriter out = response.getWriter();
        out.println("Successfully logged in " + currentUser.getUsername());
        out.flush();
        logger.debug("Forwarding to /accounts");
        request.getRequestDispatcher("/accounts").forward(request, response);
      }
    } catch (Exception e) {
      logger.error("Error logging in", e);
    } finally {
      if (session != null) {
        try { session.close(); } catch(Exception ex) {}
      }
    }

    logger.trace("Leaving doPost");
  }

}
