package uncc.goldrush.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {
  private Double amount;
  private String payee;
  private Date date;
  private Account account;
  private Integer id;
  
  public static final List<String> PAYEES = new ArrayList<String>();
  
  static {
    PAYEES.add("Harris Teeter");
    PAYEES.add("Wal-Mart");
    PAYEES.add("XBox LIVE");
    PAYEES.add("UNC Charlotte");
    PAYEES.add("Exams-R-US");
    PAYEES.add("Bi-Lo");
    PAYEES.add("Sam's Warehouse");
    PAYEES.add("Caribbean Cruises");
    PAYEES.add("Netflix");
    PAYEES.add("XM Radio");
    PAYEES.add("Gamestop");
    PAYEES.add("Amazon.com");
    PAYEES.add("CD Warehouse");
    PAYEES.add("AT&T");
    PAYEES.add("Sam Goody");
    PAYEES.add("Apple Store");
    PAYEES.add("Best Buy");
    PAYEES.add("Circuit City");
    PAYEES.add("Sears");
    PAYEES.add("Remco");
    PAYEES.add("NTB");
    PAYEES.add("Ticketmaster");
    PAYEES.add("Lidz");
    PAYEES.add("Ikea");
    PAYEES.add("Target");
    PAYEES.add("Barnes and Noble");
    PAYEES.add("REI");
    PAYEES.add("Bass Pro Shops");
    PAYEES.add("Exxon");
  }
  
  public Transaction() {}
  
  public Transaction(Date date, String payee, Double amount, Account account) {
    setDate(date);
    setPayee(payee);
    setAmount(amount);
    setAccount(account);
  }
  
  public String toString() { return String.format("Date: %1$tY-%1$tm-%1$td, Payee: %2$s, Amount: $%3$02.2f", this.getDate(), this.getPayee(), this.getAmount()); }
  
  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  
  public Date getDate() { return date; }
  public void setDate(Date date) { this.date = date; }
  
  public String getPayee() { return payee; }
  public void setPayee(String payee) { this.payee = payee; }
  
  public Double getAmount() { return amount; }
  public void setAmount(Double amount) { this.amount = amount; }
  
  public Account getAccount() { return account; }
  public void setAccount(Account account) { this.account = account; }
}
