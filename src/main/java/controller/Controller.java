package controller;

import com.jfoenix.controls.JFXTextField;
import ga.GeneticAlgorithm;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import logger.Writer;
import model.Chromosome;
import view.Board;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {

    @FXML
    private TextField mutation;

    @FXML
    private TextField offspring;

    @FXML
    private TextField generation;

    @FXML
    private TextField run;

    @FXML
    private TextField success;

    @FXML
    private TextField fail;

    @FXML
    private JFXTextField length;

    @FXML
    private JFXTextField maxRun;

    @FXML
    private JFXTextField population;

    @FXML
    private JFXTextField max;

    @FXML
    private JFXTextField rate;

    @FXML
    private Button start;

    @FXML
    private Button next;

    @FXML
    private TextArea area;

    @FXML
    private AnchorPane container;

    @FXML
    private TextField solutionCount;

    Thread thread;
    Writer logWriter;
    AtomicInteger sCount;
    Task<Void> task;

    ArrayList<Chromosome> allSolutions = new ArrayList<>();
    private boolean isAlreadyStarted = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logWriter = new Writer();
        start.setOnAction(event -> {
            if (!isAlreadyStarted) {
                runTest();
            }
        });
    }


    private void runTest() {
        task = new Task<Void>() {
            @Override
            protected Void call() {
                isAlreadyStarted = true;
                showProgress();
                Platform.runLater(() -> area.setText("Solutions:"));
                sCount = new AtomicInteger(0);
                test(Integer.parseInt(length.getText()), Integer.parseInt(maxRun.getText()),
                        Double.parseDouble(rate.getText()),
                        Integer.parseInt(max.getText()));
                isAlreadyStarted = false;
                return null;
            }
        };
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showProgress() {
        Platform.runLater(() -> Board.drawProgress(container));
    }

    private void test(int length, int maxRun, double rate, int maxGeneration) {
        logWriter = new Writer();
        GeneticAlgorithm ga = new GeneticAlgorithm(length, this);
        Platform.runLater(() -> {
            fail.setText("FAIL " + 0);
            success.setText("SUCCESS " + 0);
            run.setText("RUN " + (0));
        });
        ga.setMutation(rate);
        ga.setGeneration(maxGeneration);
        ga.setSTART_SIZE(Integer.parseInt(population.getText()));
        File file = new File("DATA");
        if (!file.exists()) {
            file.mkdirs();
        }
        String filepath = "DATA/" + LocalDate.now() + "_" + LocalTime.now().toString().replace('.', '_')
                .replace(':', '-') + "_GA-N" + length + "-" + rate + "-" + maxGeneration + ".txt";
        int failCount = 0;
        int successCount = 0;

        logParameters(length, ga);
        allSolutions = new ArrayList<>();
        AtomicInteger runs = new AtomicInteger(1);

        for (int i = 0; i < maxRun; ) {

            if (ga.algorithm()) {
                i++;
                successCount++;
                logWriter.add("Run: " + i);
                logWriter.add("Found at Generation: " + ga.getGeneration());
                logWriter.add("Population size: " + ga.getPopSize());
                logWriter.add("");

                ArrayList<Chromosome> solutions = ga.getSolutions();
                allSolutions.addAll(solutions);
                next.setOnAction(event -> {
                    sCount.getAndIncrement();
                    drawSolution(allSolutions);
                });
                drawSolution(allSolutions);
                Platform.runLater(() -> area.setText(allSolutions.toString().replace("[", "")
                        .replace("]", "")));
                for (Chromosome c : solutions) {
                    logWriter.add(c);
                    logWriter.add("");
                }
            } else {
                failCount++;
            }
            int finalFailCount = failCount;
            int finalSuccessCount = successCount;
            Platform.runLater(() -> {
                fail.setText("FAIL " + finalFailCount);
                success.setText("SUCCESS " + finalSuccessCount);
                run.setText("RUN " + (runs.getAndIncrement()));
            });

            if (failCount >= 100) {
                fail();
                break;
            }
        }
        logWriter.add("Number of Success: " + successCount);
        logWriter.add("Number of failures: " + failCount);
        logWriter.writeFile(filepath);
    }

    private void drawSolution(ArrayList<Chromosome> solutions) {
        Platform.runLater(() -> {
            try {
                int size = solutions.size();
                if (size != 0) {
                    solutionCount.setText("SOLUTION " + ((sCount.get() % size) + 1));
                    Board.drawBoard(container, solutions.get(sCount.get() % size).getTable());
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void fail() {
        if (allSolutions.size() == 0)
            Platform.runLater(() -> Board.drawFail(container));
    }

    public void logParameters(int length, GeneticAlgorithm ga) {
        logWriter.add("Genetic Algorithm");
        logWriter.add("Parameters");
        logWriter.add("MAX_LENGTH/N: " + length);
        logWriter.add("STARTING_POPULATION: " + ga.getStartSize());
        logWriter.add("MAX_Generation: " + ga.getMaxGeneration());
        logWriter.add("MATING_PROBABILITY: " + ga.getMatingProb());
        logWriter.add("MUTATION_RATE: " + ga.getMutationRate());
        logWriter.add("MIN_SELECTED_PARENTS: " + ga.getMinSelect());
        logWriter.add("MAX_SELECTED_PARENTS: " + ga.getMaxSelect());
        logWriter.add("OFFSPRING_PER_GENERATION: " + ga.getOffspring());
        logWriter.add("MINIMUM_SHUFFLES: " + ga.getShuffleMin());
        logWriter.add("MAXIMUM_SHUFFLES: " + ga.getShuffleMax());
        logWriter.add("");
    }

    public void onGenerationChange(int newGeneration) {
        Platform.runLater(() -> generation.setText("GENERATION " + newGeneration));
    }

    public void onMutationChange(int mutations) {
        Platform.runLater(() -> mutation.setText("MUTATION " + mutations));
    }

    public void onChildCountChange(int childCount) {
        Platform.runLater(() -> offspring.setText("OFFSPRING " + childCount));
    }
}
