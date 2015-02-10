package uncc.goldrush.bean;

public class User {
  private String surname;
  private String givenName;
  private String password;
  private String role;
  private String username;
  
  public User() {}
  
  public User(String username, String password,
      String role, String surname, String givenName) {
    this.username = username;
    this.password = password;
    this.role = role;
    this.surname = surname;
    this.givenName = givenName;
  }
  
  public String toString() {
    return String.format("Username: %s, password: %s, surname: %s, givenName: %s",
        username, password, surname, givenName);
  }
  
  /**
   * @return the surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
  }
  /**
   * @return the givenName
   */
  public String getGivenName() {
    return givenName;
  }
  /**
   * @param givenName the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }
  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }
  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }
  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }
  /**
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * @param role the role to set
   */
  public void setRole(String role) {
    this.role = role;
  }
  
  public boolean ownAccount(String accountNum){
	  return true;
  }

public boolean isAuthorizedBankEmployee() {
	// TODO Auto-generated method stub
	return false;
}
}
