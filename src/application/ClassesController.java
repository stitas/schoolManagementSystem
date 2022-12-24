package application;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClassesController implements Initializable{

	public List<String> selectedClassInfo = new ArrayList<String>();
	List<String> classArrayList = new ArrayList<String>();
	
	@FXML
	Label userFullName, className, classPreceptor;
	
	@FXML
	ListView<String> classList;
	
	@FXML
	TextField searchBar;
	
	@FXML
	TableView<String> classTable;
	
	public void initName(String name) {
		userFullName.setText(name);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initList();
		changeToSelectedItem();
	}
	
	public void search(ActionEvent event) {
        classList.getItems().clear();
        classList.getItems().addAll(searchList(searchBar.getText(),classArrayList));
    }

    private List<String> searchList(String searchWords, List<String> listOfStrings) {

        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }
    
    private void initList() {
		String name;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Name FROM class;";
		
		try {
			Statement statement1 = connectDB.createStatement();
			ResultSet queryResult = statement1.executeQuery(query);
			
			while (queryResult.next()) {
				name = queryResult.getString(1);
				classArrayList.add(name);
			}
			classList.getItems().addAll(classArrayList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void changeToSelectedItem() {
    	classList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				selectedClassInfo.clear();
				String preceptorFName = "";
				String preceptorLName = "";
				String selectedClass = classList.getSelectionModel().getSelectedItem();
				
				DatabaseConnection connectNow = new DatabaseConnection();
				Connection connectDB = connectNow.getConnection();
				
				String query = "SELECT * FROM class WHERE Name='" + selectedClass + "'";
				
				try {
					Statement statement1 = connectDB.createStatement();
					Statement statement2 = connectDB.createStatement();
					ResultSet queryResult = statement1.executeQuery(query);
					while (queryResult.next()) {
						className.setText(queryResult.getString(2));
						ResultSet getPreceptorName = statement2.executeQuery("SELECT Firstname, Lastname FROM Teacher WHERE idTeacher='" + queryResult.getInt(3) + "'");
						if (getPreceptorName.next()) {
							preceptorFName = getPreceptorName.getString(1);
							preceptorLName = getPreceptorName.getString(2);
							classPreceptor.setText(preceptorFName + " " + preceptorLName);
						}
						else {
							preceptorFName = "None";
						    preceptorLName = "None";
							classPreceptor.setText(preceptorFName + " " + preceptorLName);
						}
							
						selectedClassInfo.add(className.getText());
						selectedClassInfo.add(preceptorFName);
						selectedClassInfo.add(preceptorLName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
