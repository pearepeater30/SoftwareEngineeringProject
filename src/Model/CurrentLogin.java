package Model;

public class CurrentLogin {
  /**Storing the firstname, lastname, userID, userType of the Currently logged in user*/
  private String firstName = null;
  private String lastName = null;
  private int userID = 0;
  private String userType = null;
  private static CurrentLogin single_instance = null;

  /**
   * Private Default Constructor
   */
  private CurrentLogin(){
    super();
  }

  /**Getters and setters for the fields of this singleton*/
  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }



  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;

  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;

  }

  /**static method to create instance of singleton*/
  public static CurrentLogin getInstance()
  {
    if (single_instance == null){
      single_instance = new CurrentLogin();
    }


    return single_instance;
  }
}
