package com.intelidomo.configurador;

import com.intelidomo.configurador.img.Images;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Jassiel
 */
public class Main extends Application {
  public static Images img = Images.getInstance();
  public static Stage stage;
  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/Principal.fxml"));
    Scene scene = new Scene(root);
    Main.stage = stage;
    stage.getIcons().add(img.getImage("logo.png"));
    stage.setTitle("Configurador Intelidomo");
    stage.setScene(scene);
    stage.show();
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}