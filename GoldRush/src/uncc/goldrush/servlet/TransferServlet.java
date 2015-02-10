package uncc.goldrush.servlet;

import java.io.IOException;
import java.util.Date;
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
 * Servlet implementation class TransferServlet
 */
public class TransferServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory
      .getLogger(TransferServlet.class);

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    /*
     * Probably a really dumb assumption, but let's assume that if they GET,
     * then they're just viewing the form, but if they POST then they're
     * submitting the form.
     */
    logger.trace("Entering doGet");

    User user = (User) request.getSession().getAttribute("USER");
    if (user == null) {
      logger.warn("Invalid access attempt");
      request.setAttribute("MESSAGE", "You must first log in");
      request.getRequestDispatcher("/login.jsp").forward(request, response);
    } else if ("ADVISOR".equals(user.getRole())) {
      /*
       * We can't let people with the ADVISOR role transfer on behalf of their
       * clients.
       */
      logger.warn("Advisor attempting a transfer");
      request.setAttribute("MESSAGE", "You are not authorized to transfer");
      request.getRequestDispatcher("/accounts").forward(request, response);
    } else {
      /*
       * If we get here, we're fine - they're a customer, so they can transfer
       */
      SqlSession session = null;
      try {
        session = DBUtil.getSqlMapper().openSession();
        AccountMapper accounts = session.getMapper(AccountMapper.class);
        Account account = accounts.getAccount(request.getParameter("account"));
        if (account == null) {
          request.setAttribute("MESSAGE", "Invalid account");
          request.getRequestDispatcher("/accounts").forward(request, response);
        } else {
          request.setAttribute("ACCOUNT", account);
          request.setAttribute("SOURCE", request.getParameter("account"));
          request.setAttribute("PAGE", "/_transfer.jsp");
          request.getRequestDispatcher("/_template.jsp").forward(request,
              response);
        }
      } finally {
        if (session != null) {
          try {
            session.close();
          } catch (Exception ex) {
          }
        }
      }
    }
  }
 
  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    /* Get the user for this session */
    User user = (User) request.getSession().getAttribute("USER");
    
    SqlSession session = null;
    try {
      session = DBUtil.getSqlMapper().openSession();
      AccountMapper accounts = session.getMapper(AccountMapper.class);
      Account sourceAccount = getAccount(request.getParameter("source"));

        logger.info("User has access");
        sourceAccount.setBalance(sourceAccount.getBalance()
            - Double.parseDouble(request.getParameter("amount")));
        accounts.updateAccount(sourceAccount);
        Transaction transaction = new Transaction(new Date(), "TRANSFER TO " + request.getParameter("target"), Double.parseDouble(request.getParameter("amount")), sourceAccount);
        accounts.insertTransaction(transaction);
        session.commit();
        request.setAttribute("MESSAGE", "Transfer succeeded");
        request.getRequestDispatcher("/accounts").forward(request, response);
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (Exception ex) {
        }
      }
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private Account getAccount(String incomingNumber)
	{
		//this function is for training purposes only
		 Account account = null;
		 return account;
	}
}
