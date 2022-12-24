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

public class DeleteTeacherPopupController {

	String teacherFName;
	String teacherLName;
	String teacherEmail;
	
	@FXML
	Label deleteQuestion;
	
	public void getSelectedTeacherInfo(List<String> arr) {
		deleteQuestion.setText("Are you sure you want to remove " + arr.get(0) + " " + arr.get(1) + " from the database ?");
		teacherFName = arr.get(0);
		teacherLName = arr.get(1);
		teacherEmail = arr.get(3);
	}
	
	public void yesBtnOnAction(ActionEvent event) throws IOException {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "DELETE FROM teacher WHERE Firstname='" + teacherFName + "' AND Lastname='" + teacherLName + "' AND Email='" + teacherEmail + "'";
		
		try {
			Statement statement = connectDB.createStatement();
			statement.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		Parent root = loader.load();
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void noBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		Parent root = loader.load();
		
		TeachersController teachersController = loader.getController();
		teachersController.selectTeacher(teacherFName + " " + teacherLName);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
}
