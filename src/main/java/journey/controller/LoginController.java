package journey.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import journey.data.User;
import journey.data.Database;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import journey.gui.MainWindow;

import java.io.IOException;


public class LoginController {
    @FXML private TextField nameTextBox;

    @FXML private void registerUser(ActionEvent actionEvent) {
        String name = getNameTextBox();
        Database.setCurrentUser(name);
        //something to switch stages

        System.out.println("Updated User");

        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = baseLoader.load();
            Stage stage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(stage);

            stage.setTitle("Journey");
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);

            // set the min height and width so the window opens at the correct size
            stage.setMinHeight(600);
            stage.setMinWidth(900);
            MainWindow.getStage().close();
            stage.show();

        } catch(IOException e) {
            System.out.println("Exception in loading");
            e.printStackTrace();
        }
        actionEvent.consume();
    }
    @FXML private String getNameTextBox() {
        return nameTextBox.getText();
    }

    public void init(Stage stage) { ; }
}
