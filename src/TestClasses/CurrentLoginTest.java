package TestClasses;

import Model.CurrentLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurrentLoginTest {

  @Test
  public void testConstruction() throws Exception{
    CurrentLogin currentUser =  CurrentLogin.getInstance();
    currentUser.setUserID(1);
    currentUser.setUserType("Administrator");
    currentUser.setFirstName("Current");
    currentUser.setLastName("User");
    assertEquals(1,currentUser.getUserID());
    assertEquals("Administrator",currentUser.getUserType());
    assertEquals("Current", currentUser.getFirstName());
    assertEquals("User", currentUser.getLastName());
  }
}