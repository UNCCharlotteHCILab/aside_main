package uncc.goldrush.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.User;
import uncc.goldrush.dao.AccountMapper;
import uncc.goldrush.util.DBUtil;

/**
 * Servlet implementation class Trainer
 */
public class Trainer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Trainer() 
    {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		 String accountName = request.getParameter("AccountName");
		 AccountMapper accounts = getAccounts();
	        
		 if (((User) request.getSession().getAttribute("USER")).ownAccount(accountName))
		 { 
			 accounts.updateAccount(accountName, 0);
		 }
		 else
		 {
			 //do nothing. The user
		 } 
		 
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private AccountMapper getAccounts()
	{
		//this function is for training purposes only
		 AccountMapper accounts = null;
		 return accounts;
	}
	
	

}
