package view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Board {
    static double height;
    static double width;
    static Timer timer1;

    public static void drawBoard(AnchorPane container, String[][] queens) throws FileNotFoundException {
        width = container.getWidth();
        height = container.getHeight();
        draw(container, queens);
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
        if (timer1 != null) {
            timer1.cancel();
        }
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> draw(container, queens));
            }
        }, 10);
    }

    private static void draw(AnchorPane container, String[][] queens) {
        container.getChildren().clear();
        int n = queens.length;
        String path = "src/main/resources/queen.png";

        GridPane gridPane = new GridPane();
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                VBox box = new VBox();
                box.setStyle("-fx-background-color: " + (((i + j) % 2 == 0) ? "white" : "black"));
                box.setAlignment(Pos.CENTER);
                gridPane.add(box, j, i);
                if (Objects.equals("Q", queens[i][j])) {
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

    public static void drawFail(AnchorPane container) {
        container.getChildren().clear();
        TextField field = new TextField("NO SOLUTION FOUND!!!");
        field.setCursor(Cursor.DEFAULT);
        field.setFocusTraversable(false);
        field.setEditable(false);
        field.setStyle("-fx-background-color: transparent;-fx-text-fill: white");
        field.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 36));
        AnchorPane.setRightAnchor(field, 0.0);
        AnchorPane.setLeftAnchor(field, 0.0);
        AnchorPane.setTopAnchor(field, 0.0);
        AnchorPane.setBottomAnchor(field, 0.0);
        field.setAlignment(Pos.CENTER);
        container.getChildren().add(field);
    }

    public static void drawProgress(AnchorPane container) {
        double h = container.getHeight() / (double) 4;
        progress(container, h);
    }

    private static void progress(AnchorPane container, double h) {
        container.getChildren().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        AnchorPane.setRightAnchor(progressIndicator, 100.0);
        AnchorPane.setLeftAnchor(progressIndicator, 100.0);
        AnchorPane.setTopAnchor(progressIndicator, 100.0);
        AnchorPane.setBottomAnchor(progressIndicator, 100.0);
        progressIndicator.setPrefHeight(h);
        progressIndicator.setPrefWidth(h);
        container.getChildren().add(progressIndicator);
    }
}
