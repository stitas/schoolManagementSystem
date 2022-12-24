package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DeletePopupController {
	
	String studentFName;
	String studentLName;
	String studentClass;
	String studentEmail;
	
	@FXML
	Label deleteQuestion;
	
	public void getSelectedStudentInfo(List<String> arr) {
		deleteQuestion.setText("Are you sure you want to remove " + arr.get(0) + " " + arr.get(1) + " from the database ?");
		studentFName = arr.get(0);
		studentLName = arr.get(1);
		studentClass = arr.get(2);
		studentEmail = arr.get(3);
	}
	
	public void yesBtnOnAction(ActionEvent event) throws IOException {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "DELETE FROM student WHERE Firstname='" + studentFName + "' AND Lastname='" + studentLName + "' AND Email='" + studentEmail + "'";
		
		try {
			Statement statement = connectDB.createStatement();
			statement.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(studentFName + " " + studentLName + " " + studentClass);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void noBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(studentFName + " " + studentLName + " " + studentClass);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
}
