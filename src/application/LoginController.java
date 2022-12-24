package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private Button loginBtn;
	
	@FXML
	private TextField usernameInput, passwordInput;
	
	@FXML
	private Label loginHint;
	
	public void loginBtnOnAction(ActionEvent event) {
		if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
			loginHint.setText("Please enter a username and password");
		}
		else {
			validateLogin(event);
		}
	}
	
	private void validateLogin(ActionEvent event) {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String verifyLogin = "SELECT * FROM useraccounts WHERE username = '" + usernameInput.getText() +"' AND password = '" + passwordInput.getText() + "'";
	
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(verifyLogin);
			
			if (!queryResult.next()) {
				loginHint.setText("Incorrect username or password");
			}			
			else {
				loadScene(event,queryResult.getString(2),queryResult.getString(3));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadScene(ActionEvent event, String fName, String lName) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Index.fxml"));
		root = loader.load();
		
		IndexController indexController = loader.getController();
		indexController.initName(fName, lName);
		
//		root = FXMLLoader.load(getClass().getResource("Index.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
}
