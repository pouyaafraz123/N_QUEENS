package controller;

import com.jfoenix.controls.JFXTextField;
import ga.GeneticAlgorithm;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import logger.Writer;
import model.Chromosome;
import view.Board;

import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField generation;

    @FXML
    private JFXTextField length;

    @FXML
    private JFXTextField population;

    @FXML
    private JFXTextField max;

    @FXML
    private JFXTextField rate;

    @FXML
    private Button start;

    @FXML
    private TextArea area;

    @FXML
    private AnchorPane container;

    Thread thread;
    Writer logWriter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logWriter = new Writer();
        start.setOnAction(event -> runTest());
    }


    private void runTest(){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                test(Integer.parseInt(length.getText()),Double.parseDouble(rate.getText()),
                        Integer.parseInt(max.getText()));
                return null;
            }
        };

        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void test(int length,double rate,int maxGeneration) {
        GeneticAlgorithm ga = new GeneticAlgorithm(length,this);
        ga.setMutation(rate);
        ga.setGeneration(maxGeneration);
        ga.setSTART_SIZE(Integer.parseInt(population.getText()));
        String filepath = LocalDate.now()+"_"+LocalTime.now().toString().replace('.','_').replace(':','-') +"_GA-N"+length+"-"+rate+"-"+maxGeneration+".txt";
        logParameters(length,ga);
        long startTime = System.nanoTime();
        long endTime;
        long totalTime;
        if(ga.algorithm()) {
            endTime = System.nanoTime();
            totalTime = endTime - startTime;

            System.out.println("Done");
            System.out.println("time in nanoseconds: "+totalTime);
            System.out.println("Success!");

            logWriter.add("Runtime in nanoseconds: "+totalTime);
            logWriter.add("Found at Generation: "+ga.getGeneration());
            logWriter.add("Population size: "+ga.getPopSize());
            logWriter.add("");

            ArrayList<Chromosome> solutions = ga.getSolutions();
            for(Chromosome c: solutions) {
                logWriter.add(c);
                logWriter.add("");
            }
            Chromosome v = solutions.get(0);
            String[][] board = new String[v.getMaxLength()][v.getMaxLength()];
            for(int x = 0; x < v.getMaxLength(); x++) {
                board[x][v.getGene(x)] = "Q";
            }
            Platform.runLater(() -> {
                try {
                    Board.drawBoard(container,board);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("Fail!");
        }
        logWriter.writeFile(filepath);
    }

    public void logParameters(int length,GeneticAlgorithm ga) {
        logWriter.add("Genetic Algorithm");
        logWriter.add("Parameters");
        logWriter.add("MAX_LENGTH/N: "+length);
        logWriter.add("STARTING_POPULATION: "+ga.getStartSize());
        logWriter.add("MAX_Generation: "+ga.getMaxGeneration());
        logWriter.add("MATING_PROBABILITY: "+ga.getMatingProb());
        logWriter.add("MUTATION_RATE: "+ga.getMutationRate());
        logWriter.add("MIN_SELECTED_PARENTS: "+ga.getMinSelect());
        logWriter.add("MAX_SELECTED_PARENTS: "+ga.getMaxSelect());
        logWriter.add("OFFSPRING_PER_GENERATION: "+ga.getOffspring());
        logWriter.add("MINIMUM_SHUFFLES: "+ga.getShuffleMin());
        logWriter.add("MAXIMUM_SHUFFLES: "+ga.getShuffleMax());
        logWriter.add("");
    }

    public void onGenerationChange(int newGeneration){
        Platform.runLater(() -> generation.setText(String.valueOf(newGeneration)));
    }
}
