package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TeacherPopupController implements Initializable{

	List<String> teacherInfo = new ArrayList<String>();
	
	@FXML
	TextField fNameEdit, lNameEdit, emailEdit, phoneEdit, salaryEdit;
	
	@FXML
	ChoiceBox<String> subjectEdit;
	
	@FXML
	DatePicker dobEdit;
	
	@FXML
	CheckBox preceptorTEdit, preceptorFEdit;
	
	public void getSelectedTeacherInfo(List<String> arr) {
		teacherInfo.addAll(arr);
		fNameEdit.setText(teacherInfo.get(0));
		lNameEdit.setText(teacherInfo.get(1));
		if (teacherInfo.get(2) == "True") {
			preceptorTEdit.setSelected(true);
		}
		else {
			preceptorFEdit.setSelected(true);
		}
		emailEdit.setText(teacherInfo.get(3));
		dobEdit.setValue(LocalDate.parse(teacherInfo.get(4)));
		salaryEdit.setText(teacherInfo.get(5));
		phoneEdit.setText(teacherInfo.get(6));
		subjectEdit.getSelectionModel().select(teacherInfo.get(7));;
		
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
	
	public void submitBtnOnAction (ActionEvent event) throws IOException {
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
		
		
		
		String query = String.format("UPDATE teacher SET Firstname='%s', Lastname='%s', Preceptor=%d, Salary=%s, Email='%s', Phone=%s, DOB=date('%s'), Subject_id=%d WHERE Firstname='%s' AND Lastname='%s' AND Email='%s'", fNameEdit.getText(), lNameEdit.getText(), preceptor, salaryEdit.getText(), emailEdit.getText(), phoneEdit.getText(), dobEdit.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), subjectID, fNameEdit.getText(), lNameEdit.getText(), emailEdit.getText());
		
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
		teachersController.selectTeacher(teacherInfo.get(0) + " " + teacherInfo.get(1));
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
