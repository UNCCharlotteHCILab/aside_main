package uncc.goldrush.dao;

import java.sql.PreparedStatement;
import java.util.List;

import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.Entitlement;
import uncc.goldrush.bean.Transaction;
import uncc.goldrush.bean.User;

public interface AccountMapper {
  public List<Account> myAccounts(User user);
  
  public User loginUser(User user);
  
  public Account getAccount(String accountNumber);
  
  public int updateAccount(Account account);
  
  public List<Transaction> getTransactionsForAccount(Account account);

  public int createUserTable();

  public int createAccountTable();

  public int createAccountUserTable();
  
  public int createTransactionTable();

  public int insertTransaction(Transaction transaction);

  public int insertAccount(Account account);

  public int insertEntitlement(Entitlement entitlement);

  public int insertUser(User user);
  
  public void updateAccount(String accountName, int balance);
}
