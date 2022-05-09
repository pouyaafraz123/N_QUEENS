package view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Board {
    static double height;
    static double width;
    static Timer timer;
    public static void drawBoard(AnchorPane container,String[][] queens) throws FileNotFoundException {
        width = container.getWidth();
        height = container.getHeight();
        draw(container,queens);
        container.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.doubleValue();
            drawOnThread(container, queens);

        });
        container.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.doubleValue();
            drawOnThread(container, queens);
        });


    }

    private static void drawOnThread(AnchorPane container, String[][] queens) {
        if (timer!=null){
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> draw(container, queens));
            }
        },100);
    }

    private static void draw(AnchorPane container, String[][] queens) {
        container.getChildren().clear();
        int n = queens.length;
        String path = "src/main/resources/queen.png";

        GridPane gridPane = new GridPane();
        AnchorPane.setRightAnchor(gridPane,0.0);
        AnchorPane.setLeftAnchor(gridPane,0.0);
        AnchorPane.setTopAnchor(gridPane,0.0);
        AnchorPane.setBottomAnchor(gridPane,0.0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                VBox box = new VBox();
                box.setStyle("-fx-background-color: "+(((i+j)%2==0)?"white":"black"));
                box.setAlignment(Pos.CENTER);
                gridPane.add(box,j,i);
                if (Objects.equals("Q",queens[i][j])) {
                    FileInputStream input;
                    try {
                        input = new FileInputStream(path);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Image image = new Image(input);
                    ImageView imageView = new ImageView(image);
                    double top = height / (double) (2 * n);
                    double right = width / (double) (2 * n);
                    imageView.setFitHeight(top);
                    imageView.setFitWidth(right);
                    box.setPrefHeight(height * 2);
                    box.setPrefWidth(width * 2);
                    box.getChildren().add(imageView);
                }
            }
        }

        container.getChildren().add(gridPane);
    }
}
