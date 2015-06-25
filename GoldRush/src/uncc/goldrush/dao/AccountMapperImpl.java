
package uncc.goldrush.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.Entitlement;
import uncc.goldrush.bean.Transaction;
import uncc.goldrush.bean.User;

public class AccountMapperImpl implements AccountMapper{
  public List<Account> myAccounts(User user){
	  return null;
  }
  
  public User loginUser(User user){
	  return null;
  }
  
  public Account getAccount(String accountNumber){
	  return null;
  }
  
  public int updateAccount(Account account){
	  return 0;
  }
  
  public List<Transaction> getTransactionsForAccount(Account account){
	  return null;
  }

  public int createUserTable(){
	  return 0;
  }

  public int createAccountTable(){
	  return 0;
  }

  public int createAccountUserTable(){
	  return 0;
  }
  
  public int createTransactionTable(){
	  return 0;
  }

  public int insertAccount(Account account){
	  return 0;
  }

  public int insertEntitlement(Entitlement entitlement){
	  return 0;
  }

  public int insertUser(User user){
	  return 0;
  }
  

public void withdraw(String accountName, int amountToWithDraw) {
	// TODO Auto-generated method stub
	  PreparedStatement sql = null;
	  try {
		int i = sql.executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public int getAvailableAmount(String name) {
	  PreparedStatement sql = null;
	  try {
		int i = sql.executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;
}

public int insertTransaction(Transaction transaction){
	  Connection con = null;
	  Properties connectionProps = new Properties();
	    connectionProps.put("user", "jun");
	    connectionProps.put("password", "junpwd");
	  try {
		con = DriverManager.getConnection(
		          "jdbc:mysql://Account:20/",
		          connectionProps);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	//Insert transaction without prepared statement
	  
	 Statement stmt = null;
	 try {
		stmt = con.createStatement();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  String sql = "INSERT INTO transaction (accountNumber, transactionDate, payee, amount) VALUES ("
	  		+ transaction.getAccount().getAccountNumber() + ", " + transaction.getDate() + ", "
	  		+ transaction.getPayee() + ", " + transaction.getAmount() + ")";
	  try {
		stmt.executeUpdate(sql);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
/*	  String query_string = "INSERT INTO transaction (accountNumber, transactionDate, payee, amount) VALUES (?, ?, ?, ?);";
	  PreparedStatement preparedStatement = null;
	try {
		preparedStatement = con.prepareStatement(query_string);
		preparedStatement.setString(1, transaction.getAccount().getAccountNumber());
		preparedStatement.setDate(2, (Date) transaction.getDate());
		preparedStatement.setString(3, transaction.getPayee());
		preparedStatement.setDouble(4, transaction.getAmount());
		preparedStatement.executeUpdate();
		return 1;
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}*/
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;
	
}
  public void updateAccount(String accountName, int balance){
	  Connection con = null;
	  Properties connectionProps = new Properties();
	    connectionProps.put("user", "jun");
	    connectionProps.put("password", "junpwd");
	  try {
		con = DriverManager.getConnection(
		          "jdbc:mysql://Account:20/",
		          connectionProps);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  //Update account without prepared statement
	  Statement stmt = null;
	try {
		stmt = con.createStatement();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  String sql = "Update Account Set balance = " +balance + " WHERE account_name = " + accountName;
	  try {
		stmt.executeUpdate(sql);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
	  
/*	  String query_string = "UPDATE Account SET balance = ? WHERE account_name = ?;";
	  PreparedStatement preparedStatement = null;
	try {
		preparedStatement = con.prepareStatement(query_string);
		preparedStatement.setInt(1, balance);
		preparedStatement.setString(2, accountName);
		preparedStatement.executeUpdate();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}*/
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }

}
