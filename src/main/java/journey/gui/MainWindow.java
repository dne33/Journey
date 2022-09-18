package journey.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import journey.controller.LoginController;

import java.io.IOException;

/**
 * Class starts the javaFX application window
 * @author Journey dev team
 */
public class MainWindow extends Application {
    private static final Logger log = LogManager.getLogger();
    private static Stage stage;
    public static Stage getStage() {
        return stage;
    }

    /**
     * Opens the gui with the fxml content specified in resources/fxml/main
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = baseLoader.load();

            LoginController baseController = baseLoader.getController();
            baseController.init(primaryStage);
            stage = primaryStage;
            primaryStage.setTitle("Journey");
            Scene scene = new Scene(root, 600, 400);
            // scene.getStylesheets().add("src/main/resources/gui/style.css");
            primaryStage.setScene(scene);
            primaryStage.show();
            // set the min height and width so the window opens at the correct size
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(900);
            primaryStage.setMaximized(true);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Launches the FXML application, this must be called from another class (in this cass App.java) otherwise JavaFX
     * errors out and does not run
     * @param args command line arguments
     */
    public static void main(String [] args) {
        launch(args);
    }

}
