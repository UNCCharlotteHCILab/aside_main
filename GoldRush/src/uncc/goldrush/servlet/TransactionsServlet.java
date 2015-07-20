package uncc.goldrush.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.Transaction;
import uncc.goldrush.bean.User;
import uncc.goldrush.dao.AccountMapper;
import uncc.goldrush.util.DBUtil;

/**
 * Servlet implementation class TransactionsServlet
 */
public class TransactionsServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(TransactionsServlet.class);

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.trace("Entering doPost");

    SqlSession session = DBUtil.getSqlMapper().openSession();
    AccountMapper accounts = session.getMapper(AccountMapper.class);
    
    Account account = accounts.getAccount(request.getParameter("account"));
    if (account == null) {
      logger.warn("The account {} was not found", request.getParameter("account"));
      request.setAttribute("MESSAGE", "The specified account does not exist");
      logger.debug("Forwarding to /accounts");
      request.getRequestDispatcher("/accounts").forward(request, response);
    } else if (((User) request.getSession().getAttribute("USER")).ownAccount(account.getAccountNumber())){
    
      request.setAttribute("ACCOUNT", account);
      PrintWriter out = response.getWriter();
      out.println("Account " + account.toString() + " found");
      out.flush();
      logger.debug("Getting transactions");
      List<Transaction> transactions = accounts.getTransactionsForAccount(account);
      request.setAttribute("TRANSACTIONS", transactions);
      request.setAttribute("PAGE", "/_transactions.jsp");
      logger.debug("Sending to _template");
      request.getRequestDispatcher("/_template.jsp").forward(request, response);
    }

    logger.trace("Leaving doPost");
  }
}
