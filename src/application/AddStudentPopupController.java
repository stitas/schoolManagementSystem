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

public class AddStudentPopupController implements Initializable {

	List<String> studentInfo = new ArrayList<String>();
	String selectedFName;
	String selectedLName;
	String selectedClass;
	
	@FXML
	TextField fNameEdit, lNameEdit, emailEdit;
	
	@FXML
	ChoiceBox<String> classEdit, parentEdit;
	
	@FXML
	DatePicker dobEdit;
	
	@FXML
	CheckBox maleEdit, femaleEdit;
	
	@FXML
	Label addHint;
	
	public void getSelectedStudentInfo(List<String> arr) {
		if (arr.size() > 3) {
			selectedFName = arr.get(0);
			selectedLName = arr.get(1);
			selectedClass = arr.get(2);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { 
		classChoiceBoxValues();
		parentChoiceBoxValues();
	}
	
	private void classChoiceBoxValues() {
		List<String> classes = new ArrayList<String>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Name FROM class";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				classes.add(queryResult.getString(1));
			}
			classEdit.getItems().addAll(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parentChoiceBoxValues() {
		List<String> parents = new ArrayList<String>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Firstname, Lastname FROM parent";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				parents.add(queryResult.getString(1) + " " + queryResult.getString(2));
			}
			parentEdit.getItems().addAll(parents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getAvailableEmails() {
		List<String> emails = new ArrayList<String>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Email FROM Student";
		
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
	
	public void addBtnOnAction(ActionEvent event) throws IOException {
		if (fNameEdit.getText().isEmpty() || lNameEdit.getText().isEmpty() || emailEdit.getText().isEmpty() || classEdit.getSelectionModel().isEmpty() || dobEdit.getValue() == null ) {
			addHint.setText("Please enter values to boxes (Parent can be empty)");
		}
		else if (!maleEdit.isSelected() && !femaleEdit.isSelected()) {
			addHint.setText("Please select a gender");
		}
		else if (getAvailableEmails().contains(emailEdit.getText())) {
			addHint.setText("A student with this email exists");
		}
		else {
			addStudent(event);
		}
	}
	
	private void addStudent(ActionEvent event) throws IOException {
		int studentClassID = 0;
		Object parentID = 0;
		String studentGender;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		try {
			Statement statement1 = connectDB.createStatement();
			ResultSet queryResult1 = statement1.executeQuery("SELECT idClass FROM class WHERE Name='" + classEdit.getSelectionModel().getSelectedItem() + "'");
			queryResult1.next();
			studentClassID = queryResult1.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (parentEdit.getValue() != null) {
				String[] parentFullname = parentEdit.getSelectionModel().getSelectedItem().split(" ");
				Statement statement2 = connectDB.createStatement();
				ResultSet queryResult2 = statement2.executeQuery("SELECT idparent FROM parent WHERE Firstname='" + parentFullname[0] + "' AND Lastname='" + parentFullname[1] + "'");
				queryResult2.next();
				parentID = queryResult2.getInt(1);
			}
			else {
				parentID = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (maleEdit.isSelected()) {
			studentGender = "m";
		}
		else {
			studentGender = "f";
		}
		
		String query = String.format("INSERT INTO student (Firstname,Lastname,Class,Email,DOB,Parent,Gender) VALUES ('%s','%s',%d,'%s',date('%s'),%d,'%s');", fNameEdit.getText(), lNameEdit.getText(), studentClassID, emailEdit.getText(), dobEdit.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), parentID, studentGender );
		
		try {
			Statement statement = connectDB.createStatement();
			statement.execute(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(fNameEdit.getText() + " " + lNameEdit.getText() + " " + classEdit.getSelectionModel().getSelectedItem());
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void backBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(selectedFName + " " + selectedLName + " " + selectedClass);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
