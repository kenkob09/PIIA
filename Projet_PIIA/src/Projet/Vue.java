package Projet;
import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Vue extends Application {

	public void start(Stage primaryStage) throws Exception {
		//On instancie la scene
		primaryStage.setTitle("EASY PAINT");
		AnchorPane myPane = (AnchorPane)FXMLLoader.load(getClass().getResource("pro.fxml"));
		Scene scene = new Scene(myPane, 700, 500);
		Image i = new Image(getClass().getResource("icone.png").toExternalForm());
		primaryStage.getIcons().add(i); 
		//Affichage
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}