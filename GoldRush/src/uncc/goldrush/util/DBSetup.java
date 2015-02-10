package uncc.goldrush.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uncc.goldrush.bean.Account;
import uncc.goldrush.bean.Entitlement;
import uncc.goldrush.bean.Transaction;
import uncc.goldrush.bean.User;
import uncc.goldrush.dao.AccountMapper;

/**
 * Servlet implementation class DBSetup
 */
public class DBSetup implements ServletContextListener {
  private static final long serialVersionUID = 1L;
  public static final Logger logger = LoggerFactory.getLogger(DBSetup.class);

  /**
   * @see HttpServlet#HttpServlet()
   */
  public DBSetup() {
    super();
  }

  /* (non-Javadoc)
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  public void contextDestroyed(ServletContextEvent arg0) {
    logger.debug("Entering contextDestroyed");
    logger.debug("Exiting contextDestroyed");
  }

  /* (non-Javadoc)
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  public void contextInitialized(ServletContextEvent arg0) {
    logger.debug("Entering contextInitialized");
    SqlSession session = DBUtil.getSqlMapper().openSession();
    try {
      AccountMapper accountMapper = session.getMapper(AccountMapper.class);
      
      logger.info("Creating user table");
      accountMapper.createUserTable();
      
      /* Add some users */
      List<User> users = new ArrayList<User>();
      users.add(new User("billchu","billchu","CUSTOMER","chu","bill"));
      users.add(new User("merrill","merrill","ADVISOR","lynch","merrill"));
      users.add(new User("wills","wills","CUSTOMER","stranathan","will"));
      users.add(new User("smithb","smithb","ADVISOR","barney","smith"));
      users.add(new User("willq","willq","CUSTOMER","quinley","will"));
      users.add(new User("efhutton","efhutton","ADVISOR","Hutton","E.F."));
      for(User user : users) {
        accountMapper.insertUser(user);
      }

      /* Generate some accounts */
      logger.info("Creating account table");
      accountMapper.createAccountTable();
      
      List<Account>accounts = new ArrayList<Account>();
      accounts.add(new Account("204938636901", "PRIMARY CHECKING", new Double(2068.14)));
      accounts.add(new Account("204931309419", "PRIMARY SAVINGS", new Double(53045.72)));
      accounts.add(new Account("204995387571", "MONEY MARKET", new Double(13995.73)));
      accounts.add(new Account("204931089304", "CHECKING", new Double(11178.44)));
      accounts.add(new Account("204961955734", "VACATION SAVINGS", new Double(29655.91)));
      accounts.add(new Account("204982981224", "KIDS COLLEGE FUND", new Double(77397.40)));
      accounts.add(new Account("204937493142", "CHECKBOOK", new Double(86197.73)));
      accounts.add(new Account("204911709117", "SPENDING ACCOUNT", new Double(15142.82)));
      accounts.add(new Account("204917540900", "SECONDARY CHECKING", new Double(73337.44)));
      accounts.add(new Account("204994890106", "PIGGY BANK", new Double(13604.84)));
      accounts.add(new Account("204911511094", "CAR SAVINGS", new Double(10440.59)));
      accounts.add(new Account("204964914939", "BUSINESS CHECKING", new Double(16557.31)));
      accounts.add(new Account("204915545375", "INVESTMENT CAPITAL", new Double(86101.48)));
      accounts.add(new Account("204921910877", "ANNIVERSARY CRUISE", new Double(60108.06)));
      accounts.add(new Account("204917401281", "BOOK ADVANCE", new Double(71468.53)));
      accounts.add(new Account("204931212246", "NEST EGG", new Double(56666.04)));
      logger.info("Adding accounts");
      for (Account account : accounts) {
        accountMapper.insertAccount(account);
      }
      
      logger.info("Calling ibatis uncc.goldrush.dao.AccountMapper.createAccountUser");
      accountMapper.createAccountUserTable();
      
      accountMapper.insertEntitlement(new Entitlement(accounts.get(0), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(0), users.get(1)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(1), users.get(2)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(1), users.get(3)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(2), users.get(4)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(2), users.get(5)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(3), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(3), users.get(1)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(4), users.get(2)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(4), users.get(3)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(5), users.get(4)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(5), users.get(5)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(6), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(6), users.get(1)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(7), users.get(2)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(7), users.get(3)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(8), users.get(4)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(8), users.get(5)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(9), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(9), users.get(1)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(10), users.get(2)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(10), users.get(3)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(11), users.get(4)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(11), users.get(5)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(12), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(12), users.get(1)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(13), users.get(2)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(13), users.get(3)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(14), users.get(4)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(14), users.get(5)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(15), users.get(0)));
      accountMapper.insertEntitlement(new Entitlement(accounts.get(15), users.get(1)));
      
      logger.info("Creating Transactions table");
      accountMapper.createTransactionTable();
      
      logger.info("Setting up transactions");
      Random rnd = new Random();
      for (Account account : accounts) {
        for (int i = 0; i < 25; i++) {
          String payee = Transaction.PAYEES.get(rnd.nextInt(Transaction.PAYEES.size()));
          Calendar date = Calendar.getInstance();
          date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - rnd.nextInt(30));
          Double amount = Math.round(rnd.nextDouble() * 10000.0) / 100.0;
          Transaction transaction = new Transaction(date.getTime(), payee, amount, account);
          logger.info("Adding transaction {}", transaction.toString());
          accountMapper.insertTransaction(transaction);
        }
      }
    } catch (Exception e) {
      logger.error("Error establishing connection", e);
    } finally {
    }
  }
}
