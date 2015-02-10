package uncc.goldrush.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LogoutServlet
 */
public class LogoutServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.trace("Entering doGet");

    response.setDateHeader("Expires", 0);
    response.setHeader("Cache-control", "no-store");
    response.setHeader("Pragma", "no-cache");

    logger.info("Invalidating session {}", request.getSession().getId());
    request.getSession().invalidate();
    request.setAttribute("MESSAGE", "Successfully logged out");
    request.getRequestDispatcher("/login.jsp").forward(request, response);

    logger.trace("Leaving doGet");
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

}
