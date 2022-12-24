package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentPopupController implements Initializable {
	
	List<String> studentInfo = new ArrayList<String>();
	
	@FXML
	TextField fNameEdit, lNameEdit, emailEdit;
	
	@FXML
	ChoiceBox<String> classEdit, parentEdit;
	
	@FXML
	DatePicker dobEdit;
	
	@FXML
	CheckBox maleEdit, femaleEdit;

	public void getSelectedStudentInfo(List<String> arr) {
		studentInfo.addAll(arr);
		fNameEdit.setText(studentInfo.get(0));
		lNameEdit.setText(studentInfo.get(1));
		emailEdit.setText(studentInfo.get(3));
		classEdit.getSelectionModel().select(studentInfo.get(2));;
		parentEdit.getSelectionModel().select(studentInfo.get(4));
		dobEdit.setValue(LocalDate.parse(studentInfo.get(7)));
		if ("Male".equals(studentInfo.get(5))) {
			maleEdit.setSelected(true);
		}
		else {
			femaleEdit.setSelected(true);
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
	
	public void submitEditBtnOnAction(ActionEvent event) throws IOException {
		Map<String, String> studentInfoMap = new HashMap<String, String>();
		studentInfoMap.put("firstname", fNameEdit.getText());
		studentInfoMap.put("lastname", lNameEdit.getText());
		studentInfoMap.put("email", emailEdit.getText());
		studentInfoMap.put("class", classEdit.getValue());
		System.out.println(fNameEdit.getText());
		if (parentEdit.getValue() != "None") {
			studentInfoMap.put("parent", parentEdit.getValue());
		}
		studentInfoMap.put("dob", dobEdit.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		if (maleEdit.isSelected()) {
			studentInfoMap.put("gender", "m");
		}
		else {
			studentInfoMap.put("gender", "f");
		}
		
		int studentClassID = 0;
		Object parentID = 0;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		try {
			Statement statement1 = connectDB.createStatement();
			ResultSet queryResult1 = statement1.executeQuery("SELECT idClass FROM class WHERE Name='" + studentInfoMap.get("class") + "'");
			queryResult1.next();
			studentClassID = queryResult1.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (studentInfoMap.containsKey("parent")) {
				String[] parentFullname = studentInfoMap.get("parent").split(" ");
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
		
		
		String query = String.format("UPDATE student SET Firstname='%s', Lastname='%s', Class=%d, Email='%s', DOB=date('%s'), Parent=%d, Gender='%s' WHERE Firstname='%s' AND Lastname='%s' AND Email='%s'", studentInfoMap.get("firstname"),studentInfoMap.get("lastname"),studentClassID,studentInfoMap.get("email"),studentInfoMap.get("dob"),parentID,studentInfoMap.get("gender"),studentInfoMap.get("firstname"),studentInfoMap.get("lastname"),studentInfoMap.get("email"));
		
		try {
			Statement statement3 = connectDB.createStatement();
			statement3.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(studentInfoMap.get("firstname") + " " + studentInfoMap.get("lastname") + " " + studentInfoMap.get("class"));
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void backBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.selectStudent(studentInfo.get(0) + " " + studentInfo.get(1) + " " + studentInfo.get(2));
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
