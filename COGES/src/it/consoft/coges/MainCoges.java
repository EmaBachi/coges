package it.consoft.coges;

import java.awt.Dimension;

import it.consoft.coges.model.Model;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class MainCoges extends Application {
	
	Scene scene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("coges.fxml"));
			BorderPane root = (BorderPane)loader.load();
			
			Model model = new Model();
			CogesController controller = loader.getController();
			controller.setModel(model);
			controller.inizializzazione();
			controller.assegnaControllerRettifiche();
			controller.assegnaControllerStoricoRettifiche();
			controller.assegnaControllerModificaAssociazione();
			
			Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

			scene = new Scene(root, dim.getWidth()-100, dim.getHeight()-100);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("COGES");
			primaryStage.show();
			
			controller.setStage(primaryStage);
			controller.impostazionitabella();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Scene getScene(){
		return scene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}