package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class IndexController implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private Label studentCount, teacherCount, avgSalary, bookCount, userFullName, indexTime, indexDate;
	
	@FXML
	private PieChart genderPieChart;
	
	@FXML
	private BarChart<String,Integer> salaryBarChart;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initStudentCount();
		initTeacherCount();
		initAvgSalary();
		initBooks();
		initClock();
		initPieChart();
		initBarChart();
	}
	
	private void initStudentCount() {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT count(*) FROM student";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while(queryResult.next()) {
				studentCount.setText(Integer.toString(queryResult.getInt(1)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initTeacherCount() {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT count(*) FROM teacher";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while(queryResult.next()) {
				teacherCount.setText(Integer.toString(queryResult.getInt(1)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initAvgSalary() {
		int count = 0;
		int salarySum = 0;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Salary FROM teacher;";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while(queryResult.next()) {
				count++;
				salarySum = salarySum + queryResult.getInt(1);
			}
			String salary = Double.toString(salarySum/count) + "$";
			avgSalary.setText(salary);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initName(String fname, String lname) {
		userFullName.setText(fname + " " + lname);
	}
	
	private void initClock() {
		AnimationTimer timer = new AnimationTimer() {
		    @Override
		    public void handle(long now) {
		        indexTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
		        indexDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("LLLL dd, eeee")));
		    }
		};
		timer.start();
	}
	
	private void initBooks() {
		int bookSum = 0;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Units FROM books";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				bookSum = bookSum + queryResult.getInt(1);
			}
			bookCount.setText(Integer.toString(bookSum));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initPieChart() {
		int maleCount = 0;
		int femaleCount = 0;
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT gender FROM student";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while(queryResult.next()) {
				if ("m".equals(queryResult.getString(1))) {
					maleCount++;
				}
				else {
					femaleCount++;
				}
			}
			ObservableList<PieChart.Data> pieChartData =
	                FXCollections.observableArrayList(
	                new PieChart.Data("Male", maleCount),
	                new PieChart.Data("Female", femaleCount));
			genderPieChart.setData(pieChartData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initBarChart() {
		Map<String,Integer> salaryCount = new HashMap<String,Integer>();
		XYChart.Series<String, Integer> series = new Series<String, Integer>();
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();
		
		String query = "SELECT Salary FROM teacher";
		
		try {
			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(query);
			
			while (queryResult.next()) {
				if (salaryCount.containsKey(Integer.toString(queryResult.getInt(1)))) {
					salaryCount.put(Integer.toString(queryResult.getInt(1)), salaryCount.get(Integer.toString(queryResult.getInt(1))) + 1);
				}
				else {
					salaryCount.put(Integer.toString(queryResult.getInt(1)), 1);
				}		
			}
			for (Map.Entry<String, Integer> entry : salaryCount.entrySet()) {
				series.getData().add(new Data<String, Integer>(entry.getKey(),entry.getValue()));
			}
			salaryBarChart.getData().add(series);
			salaryBarChart.setLegendVisible(false);
			salaryBarChart.getYAxis().setTickLabelFill(Color.WHITE);
			salaryBarChart.getXAxis().setTickLabelFill(Color.WHITE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void studentsBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Students.fxml"));
		root = loader.load();
		
		StudentsController studentsController = loader.getController();
		studentsController.initName(userFullName.getText());
//		root = FXMLLoader.load(getClass().getResource("Index.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void teachersBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Teachers.fxml"));
		root = loader.load();
		
		TeachersController teachersController = loader.getController();
		teachersController.initName(userFullName.getText());
//		root = FXMLLoader.load(getClass().getResource("Index.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void classesBtnOnAction(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Classes.fxml"));
		root = loader.load();
		
		ClassesController classesController = loader.getController();
		classesController.initName(userFullName.getText());;
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void subjectsBtnOnAction(ActionEvent event) {
		System.out.println("subjec");
	}
}
