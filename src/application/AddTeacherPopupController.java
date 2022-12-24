package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTeacherPopupController implements Initializable{

	String selectedFName;
	String selectedLName;
	
	@FXML
	TextField fNameEdit, lNameEdit, emailEdit, phoneEdit, salaryEdit;
	
	@FXML
	ChoiceBox<String> subjectEdit;
	
	@FXML
	DatePicker dobEdit;
	
	@FXML
	CheckBox preceptorTEdit, preceptorFEdit;
	
	@FXML
	Label addHint;
	
	public void getSelectedTeacherInfo(List<String> arr) {
		if (arr.size() > 2) {
			selectedFName = arr.get(0);
			selectedLName = arr.get(1);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		subjectChoiceBoxValues();
	}
	
	private void subjectChoiceBoxValues() {
		List<String> subjects = new ArrayList<String>();
		
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Name FROM subject";
		
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				subjects.add(queryResult.getString(1));
			}
			subjectEdit.getItems().addAll(subjects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getAvailableEmails() {
		List<String> emails = new ArrayList<String>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Email FROM teacher";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				emails.add(queryResult.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emails;
	}
	
	private List<String> getAvailablePhoneNumbers() {
		List<String> phone = new ArrayList<String>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Phone FROM teacher";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				phone.add(queryResult.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phone;
	}
	
	public void addBtnOnAction(ActionEvent event) throws IOException {
		if (fNameEdit.getText().isEmpty() || lNameEdit.getText().isEmpty() || emailEdit.getText().isEmpty() || phoneEdit.getText().isEmpty() || salaryEdit.getText().isEmpty() || subjectEdit.getSelectionModel().isEmpty() || dobEdit.getValue() == null) {
			addHint.setText("Please fill in all of the fields");
		}
		else if (!preceptorTEdit.isSelected() && !preceptorFEdit.isSelected()) {
			addHint.setText("Please choose if the teacher is a preceptor");
		}
		else if (getAvailableEmails().contains(emailEdit.getText())) {
			addHint.setText("A teacher with this email exists");
		}
		else if (getAvailablePhoneNumbers().contains(phoneEdit.getText())) {
			addHint.setText("A teacher with this phone number exists");
		}
		else {
			addTeacher(event);
		}
	}

	private void addTeacher(ActionEvent event) throws IOException {
		int preceptor = 0;
		int subjectID = 0; 
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		if (preceptorTEdit.isSelected()) {
			preceptor = 1;
		}
		else {
			preceptor = 0;
		}
		
		try {
			Statement statement1 = connectDB.createStatement();
			ResultSet getSubjectID = statement1.executeQuery("SELECT idsubject FROM subject WHERE Name='" + subjectEdit.getSelectionModel().getSelectedItem() + "'");
			getSubjectID.next();
			subjectID = getSubjectID.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String query = String.format("INSERT INTO teacher (Firstname,Lastname,Preceptor,Salary,Email,Phone,DOB,Subject_id) VALUES ('%s','%s',%d,%s,'%s',%s,date('%s'),%s)", fNameEdit.getText(), lNameEdit.getText(), preceptor, salaryEdit.getText(), emailEdit.getText(), phoneEdit.getText(), dobEdit.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), subjectID);
		
		try {
			Statement statement2 = connectDB.createStatement();
			statement2.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		Parent root = loader.load();
		
		TeachersController teachersController = loader.getController();
		teachersController.selectTeacher(fNameEdit.getText() + " " + lNameEdit.getText());
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void backBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		Parent root = loader.load();
		
		TeachersController teachersController = loader.getController();
		teachersController.selectTeacher(selectedFName + " " + selectedLName);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
