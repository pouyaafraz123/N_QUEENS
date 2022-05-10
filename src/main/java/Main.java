import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(new File("src/main/resources/View.fxml").toURI().toURL());
        Scene scene = new Scene(loader.load(),1200,650);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.F11)){
                primaryStage.setFullScreen(true);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
