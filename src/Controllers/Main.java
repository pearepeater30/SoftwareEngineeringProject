package Controllers;

import Model.CurrentLogin;
import Tools.SQLiteConnect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Main extends Application {
  @Override
  public void start(Stage stage)throws IOException {

    Parent root = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));

    // Create the Scene
    Scene scene = new Scene(root);
    // Set the Scene to the Stage
    stage.setScene(scene);
    // Set the Title to the Stage
    stage.setTitle("E-Commerce Example");
    stage.setMaximized(true);
    stage.setMinWidth(1300);
    stage.setMinHeight(900);
    // Display the Stage
    stage.show();
  }

  @Override
  public void stop(){
    System.out.println("Stage is closing");
    /**Logout user upon closing window*/
    Connection conn;
    try {
      conn = SQLiteConnect.ConnectDB();
      String firstNameUserSession = CurrentLogin.getInstance().getFirstName();
      String lastNameUserSession = CurrentLogin.getInstance().getLastName();
      int userIDUserSession = CurrentLogin.getInstance().getUserID();
      String userTypeUserSession = CurrentLogin.getInstance().getUserType();
      PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM USERSESSION WHERE FIRSTNAME=? and LASTNAME=? and USERID=? and USERTYPE=?");
      stmt2.setString(1, firstNameUserSession);
      stmt2.setString(2, lastNameUserSession);
      stmt2.setInt(3, userIDUserSession);
      stmt2.setString(4, userTypeUserSession);
      stmt2.executeUpdate();
      CurrentLogin.getInstance().setFirstName(null);
      CurrentLogin.getInstance().setLastName(null);
      CurrentLogin.getInstance().setUserID(0);
      CurrentLogin.getInstance().setUserType(null);
      conn.close();
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String args[]){
    launch(args);
  }
}