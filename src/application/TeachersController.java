package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TeachersController implements Initializable {

	public List<String> selectedTeacherInfo = new ArrayList<String>();
	List<String> teachers = new ArrayList<String>();
	
	@FXML
	private ListView<String> teacherList;
	
	@FXML
	private Label userFullName, teacherName, teacherPreceptor, teacherEmail, teacherDOB, teacherSalary, teacherPhoneNumber, teacherSubject;
	
	@FXML
	private TextField searchBar;
	
	
	public void initName(String name) {
		userFullName.setText(name);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initList();	
		changeToSelectedItem();
	}
	
	public void search(ActionEvent event) {
        teacherList.getItems().clear();
        teacherList.getItems().addAll(searchList(searchBar.getText(),teachers));
    }

    private List<String> searchList(String searchWords, List<String> listOfStrings) {

        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }
	
	private void initList() {
		String fname;
		String lname;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Firstname, Lastname FROM teacher;";
		
		try {
			Statement statement1 = connectDB.createStatement();
			ResultSet queryResult = statement1.executeQuery(query);
			
			while (queryResult.next()) {
				fname = queryResult.getString(1);
				lname = queryResult.getString(2);
				teachers.add(fname + " " + lname);
			}
			teacherList.getItems().addAll(teachers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeToSelectedItem() {
    	teacherList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				selectedTeacherInfo.clear();
				String selectedTeacher = teacherList.getSelectionModel().getSelectedItem();
				String[] splitTeacherInfo = selectedTeacher.split(" ");
				
				DatabaseConnection connectNow = new DatabaseConnection();
				Connection connectDB = connectNow.getConnection();
				
				String query = "SELECT * FROM teacher WHERE Firstname='" + splitTeacherInfo[0] + "' AND Lastname='" + splitTeacherInfo[1] + "'";
				
				try {
					Statement statement1 = connectDB.createStatement();
					Statement statement2 = connectDB.createStatement();
					ResultSet queryResult = statement1.executeQuery(query);
					while (queryResult.next()) {
						teacherName.setText(splitTeacherInfo[0] + " " + splitTeacherInfo[1]);
						if (queryResult.getInt(4) == 1) {
							teacherPreceptor.setText("True");
						}
						else {
							teacherPreceptor.setText("False");
						}
						teacherEmail.setText(queryResult.getString(6));
						teacherDOB.setText(queryResult.getDate(8).toString());
						teacherSalary.setText(Integer.toString(queryResult.getInt(5)));
						teacherPhoneNumber.setText(Integer.toString(queryResult.getInt(7)));
						ResultSet teacherSubjectResult = statement2.executeQuery("SELECT Name FROM subject WHERE idsubject='" + queryResult.getInt(9) + "'");
						teacherSubjectResult.next();
						teacherSubject.setText(teacherSubjectResult.getString(1));
						
						selectedTeacherInfo.add(splitTeacherInfo[0]);
						selectedTeacherInfo.add(splitTeacherInfo[1]);
						selectedTeacherInfo.add(teacherPreceptor.getText());
						selectedTeacherInfo.add(teacherEmail.getText());
						selectedTeacherInfo.add(teacherDOB.getText());
						selectedTeacherInfo.add(teacherSalary.getText());
						selectedTeacherInfo.add(teacherPhoneNumber.getText());
						selectedTeacherInfo.add(teacherSubject.getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
	
	public void selectTeacher(String name) {
    	teacherList.getSelectionModel().select(name);
    }
	
	public void editBtnOnAction (ActionEvent event) throws IOException {
    	if (selectedTeacherInfo.size() > 0) {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("TeacherPopUp.fxml"));
    		Parent root = loader.load();
    		
    		TeacherPopupController tpc = loader.getController();
            tpc.getSelectedTeacherInfo(selectedTeacherInfo);
    		
    		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    		Scene scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	}
	}
	
	public void deleteBtnOnAction (ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteTeacherPopUp.fxml"));
		Parent root = loader.load();
		
		DeleteTeacherPopupController dtpc = loader.getController();
        dtpc.getSelectedTeacherInfo(selectedTeacherInfo);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
    }
	
	public void addBtnOnAction (ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AddTeacherPopUp.fxml"));
		Parent root = loader.load();
		
		AddTeacherPopupController atpc = loader.getController();
        atpc.getSelectedTeacherInfo(selectedTeacherInfo);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void mainBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Index.fxml"));
		Parent root = loader.load();
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void studentsBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		Parent root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.initName(userFullName.getText());
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
