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

public class StudentsController implements Initializable {

	public List<String> selectedStudentInfo = new ArrayList<String>();
	List<String> students = new ArrayList<String>();
	
	@FXML
	private ListView<String> studentList;
	
	@FXML
	private Label userFullName, studentName, studentClass, studentEmail, studentDOB, studentParent, studentGender, studentPreceptor;
	
	@FXML
	private TextField searchBar;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initList();
		changeToSelectedItem();
	}
	
	private void initList() {
		String fname;
		String lname;
		String studentClass;
		int idClass;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Firstname, Lastname, Class FROM student;";
		
		try {
			Statement statement1 = connectDB.createStatement();
			Statement statement2 = connectDB.createStatement();
			ResultSet queryResult = statement1.executeQuery(query);
			
			while (queryResult.next()) {
				fname = queryResult.getString(1);
				lname = queryResult.getString(2);
				idClass = queryResult.getInt(3);
				ResultSet className = statement2.executeQuery("SELECT Name FROM class WHERE idClass = " + Integer.toString(idClass));
				className.next();
				studentClass = className.getString(1);
				students.add(fname + " " + lname + " " + studentClass);
			}
			studentList.getItems().addAll(students);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initName(String name) {
		userFullName.setText(name);
	}
	
	public void search(ActionEvent event) {
        studentList.getItems().clear();
        studentList.getItems().addAll(searchList(searchBar.getText(),students));
    }

    private List<String> searchList(String searchWords, List<String> listOfStrings) {

        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }
    
    private void changeToSelectedItem() {
    	studentList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				selectedStudentInfo.clear();
				String selectedStudent = studentList.getSelectionModel().getSelectedItem();
				String[] splitStudentInfo = selectedStudent.split(" ");
				
				DatabaseConnection connectNow = new DatabaseConnection();
				Connection connectDB = connectNow.getConnection();
				
				String query = "SELECT * FROM student WHERE Firstname='" + splitStudentInfo[0] + "' AND Lastname='" + splitStudentInfo[1] + "'";
				
				try {
					Statement statement1 = connectDB.createStatement();
					Statement statement2 = connectDB.createStatement();
					Statement statement3 = connectDB.createStatement();
					Statement statement4 = connectDB.createStatement();
					ResultSet queryResult = statement1.executeQuery(query);
					while (queryResult.next()) {
						studentName.setText(splitStudentInfo[0] + " " + splitStudentInfo[1]);
						studentClass.setText(splitStudentInfo[2]);
						studentEmail.setText(queryResult.getString(5));
						studentDOB.setText(queryResult.getDate(6).toString());
						if (queryResult.getInt(7) != 0) {
							ResultSet getParentName = statement2.executeQuery("SELECT * FROM parent WHERE idparent=" + queryResult.getInt(7));
							getParentName.next();
							studentParent.setText(getParentName.getString(2) + " " + getParentName.getString(3));
						}
						else {
							studentParent.setText("None");
						}
						if ("m".equals(queryResult.getString(8))) {
							studentGender.setText("Male");
						}
						else {
							studentGender.setText("Female");
						}
						ResultSet getPreceptorID = statement3.executeQuery("SELECT Teacher FROM Class WHERE idclass=" + queryResult.getInt(4));
						getPreceptorID.next();
						if (getPreceptorID.getInt(1) != 0) {
							ResultSet getPreceptorName = statement4.executeQuery("SELECT Firstname, Lastname FROM Teacher WHERE idTeacher="+getPreceptorID.getInt(1));
							getPreceptorName.next();
							studentPreceptor.setText(getPreceptorName.getString(1) + " " + getPreceptorName.getString(2));
						}else {
							studentPreceptor.setText("None");
						}
						selectedStudentInfo.add(splitStudentInfo[0]);
						selectedStudentInfo.add(splitStudentInfo[1]);
						selectedStudentInfo.add(splitStudentInfo[2]);
						selectedStudentInfo.add(studentEmail.getText());
						selectedStudentInfo.add(studentParent.getText());
						selectedStudentInfo.add(studentGender.getText());
						selectedStudentInfo.add(studentPreceptor.getText());
						selectedStudentInfo.add(studentDOB.getText());
						//selectedStudentInfo.forEach((i) -> System.out.println(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
    
    public void selectStudent(String name) {
    	studentList.getSelectionModel().select(name);
    }
	
    public void editBtnOnAction (ActionEvent event) throws IOException {
    	if (selectedStudentInfo.size() > 0) {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentPopUp.fxml"));
    		Parent root = loader.load();
    		
    		StudentPopupController spc = loader.getController();
            spc.getSelectedStudentInfo(selectedStudentInfo);
    		
    		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    		Scene scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	}
    }
    
    public void deleteBtnOnAction (ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("DeletePopUp.fxml"));
		Parent root = loader.load();
		
		DeletePopupController dpc = loader.getController();
        dpc.getSelectedStudentInfo(selectedStudentInfo);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
    }
    
    public void addBtnOnAction (ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("AddStudentPopUp.fxml"));
		Parent root = loader.load();
		
		AddStudentPopupController aspc = loader.getController();
        aspc.getSelectedStudentInfo(selectedStudentInfo);
		
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
    
    public void teachersBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		Parent root = loader.load();
		
		TeachersController teachersController = loader.getController();
		teachersController.initName(userFullName.getText());
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
    
}
