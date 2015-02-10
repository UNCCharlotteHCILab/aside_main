package uncc.goldrush.bean;

public class Account {
  private String accountNumber;
  private String nickname;
  private Double balance;

  public Account() {
  }

  public Account(String accountNumber, String nickname, Double balance) {
    this.accountNumber = accountNumber;
    this.nickname = nickname;
    this.balance = balance;
  }

  public String toString() {
    return String.format("%s (%s) balance $%,02.2f", nickname, accountNumber, balance);
  }

  /**
   * @return the accountNumber
   */
  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * @param accountNumber the accountNumber to set
   */
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  /**
   * @return the balance
   */
  public Double getBalance() {
    return balance;
  }

  /**
   * @param balance the balance to set
   */
  public void setBalance(Double balance) {
    this.balance = balance;
  }

  /**
   * @return the nickname
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * @param nickname the nickname to set
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
 
}
