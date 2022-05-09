package ga;

import controller.Controller;
import model.Chromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class GeneticAlgorithm {
    private final int MAX_LENGTH;
    private int START_SIZE;
    private int MAX_GENERATION;
    private final double MATING_PROBABILITY;
    private double MUTATION_RATE;
    private final int MIN_SELECT;
    private final int MAX_SELECT;
    private final int OFFSPRING_PER_GENERATION;
    private final int MINIMUM_SHUFFLES;
    private final int MAXIMUM_SHUFFLES;
    private int nextMutation;
    private ArrayList<Chromosome> population;
    private ArrayList<Chromosome> solutions;
    private Random rand;
    private int childCount;
    private int mutations;
    private int generation;
    private int populationSize;
    private Controller controller;

    public GeneticAlgorithm(int n, Controller controller) {
        MAX_LENGTH = n;
        START_SIZE = 40;
        MAX_GENERATION = 1000;
        MATING_PROBABILITY = 0.7;
        MUTATION_RATE = 0.001;
        MIN_SELECT = 10;
        MAX_SELECT = 30;
        OFFSPRING_PER_GENERATION = 20;
        MINIMUM_SHUFFLES = 8;
        MAXIMUM_SHUFFLES = 20;
        generation = 0;
        populationSize = 0;
        this.controller = controller;
    }

    public boolean algorithm() {
        population = new ArrayList<>();
        solutions = new ArrayList<>();
        rand = new Random();
        nextMutation = 0;
        childCount = 0;
        mutations = 0;
        generation = 0;
        populationSize = 0;

        boolean done = false;
        Chromosome chromosome;
        nextMutation = getRandomNumber(0, (int) Math.round(1.0 / MUTATION_RATE));

        initialize();

        while (!done) {
            populationSize = population.size();

            for (int i = 0; i < populationSize; i++) {
                chromosome = population.get(i);
                if ((chromosome.getConflicts() == 0)) {
                    done = true;
                }
            }

            if (generation == MAX_GENERATION) {
                done = true;
            }
            getFitness();
            rouletteSelection();
            mating();
            prepNextGeneration();
            generation++;
            controller.onGenerationChange(generation);
        }

        if (generation >= MAX_GENERATION) {
            System.out.println("No solution found");
            done = false;
        } else {
            populationSize = population.size();
            for (int i = 0; i < populationSize; i++) {
                chromosome = population.get(i);
                if (chromosome.getConflicts() == 0) {
                    solutions.add(chromosome);
                    printSolution(chromosome);
                }
            }
        }
        System.out.println("done.");

        System.out.println("Completed " + generation + " Generations.");
        System.out.println("Encountered " + mutations + " mutations in " + childCount + " offspring.");

        return done;
    }

    public void mating() {
        int getRand;
        int parentA;
        int parentB;
        int newIndex1;
        int newIndex2;
        Chromosome newChromosome1;
        Chromosome newChromosome2;

        for (int i = 0; i < OFFSPRING_PER_GENERATION; i++) {
            parentA = chooseParent();
            getRand = getRandomNumber(0, 100);
            if (getRand <= MATING_PROBABILITY * 100) {
                parentB = chooseParent(parentA);
                newChromosome1 = new Chromosome(MAX_LENGTH);
                newChromosome2 = new Chromosome(MAX_LENGTH);
                population.add(newChromosome1);
                newIndex1 = population.indexOf(newChromosome1);
                population.add(newChromosome2);
                newIndex2 = population.indexOf(newChromosome2);

                partiallyMappedCrossover(parentA, parentB, newIndex1, newIndex2);

                if (childCount - 1 == nextMutation) {
                    exchangeMutation(newIndex1, 1);
                } else if (childCount == nextMutation) {
                    exchangeMutation(newIndex2, 1);
                }

                population.get(newIndex1).computeConflicts();
                population.get(newIndex2).computeConflicts();

                childCount += 2;


                if (childCount % (int) Math.round(1.0 / MUTATION_RATE) == 0) {
                    nextMutation = childCount + getRandomNumber(0, (int) Math.round(1.0 / MUTATION_RATE));
                }
            }
        }
    }

    public void partiallyMappedCrossover(int chromosomeA, int chromosomeB, int child1, int child2) {
        int j;
        int item1;
        int item2;
        int pos1 = 0;
        int pos2 = 0;
        Chromosome firstChromosome = population.get(chromosomeA);
        Chromosome secondChromosome = population.get(chromosomeB);
        Chromosome newChromosome1 = population.get(child1);
        Chromosome newChromosome2 = population.get(child2);
        int crossPoint1 = getRandomNumber(0, MAX_LENGTH - 1);
        int crossPoint2 = getExclusiveRandomNumber(MAX_LENGTH - 1, crossPoint1);


        if (crossPoint2 < crossPoint1) {
            j = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = j;
        }

        for (int i = 0; i < MAX_LENGTH; i++) {
            newChromosome1.setGene(i, firstChromosome.getGene(i));
            newChromosome2.setGene(i, secondChromosome.getGene(i));
        }

        for (int i = crossPoint1; i <= crossPoint2; i++) {
            item1 = firstChromosome.getGene(i);
            item2 = secondChromosome.getGene(i);


            for (j = 0; j < MAX_LENGTH; j++) {
                if (newChromosome1.getGene(j) == item1) {
                    pos1 = j;
                } else if (newChromosome1.getGene(j) == item2) {
                    pos2 = j;
                }
            }

            if (item1 != item2) {
                newChromosome1.setGene(pos1, item2);
                newChromosome1.setGene(pos2, item1);
            }


            for (j = 0; j < MAX_LENGTH; j++) {
                if (newChromosome2.getGene(j) == item2) {
                    pos1 = j;
                } else if (newChromosome2.getGene(j) == item1) {
                    pos2 = j;
                }
            }


            if (item1 != item2) {
                newChromosome2.setGene(pos1, item1);
                newChromosome2.setGene(pos2, item2);
            }

        }
    }

    public int chooseParent() {
        int parent = 0;
        Chromosome chromosome;
        boolean done = false;

        while (!done) {
            parent = getRandomNumber(0, population.size() - 1);
            chromosome = population.get(parent);
            if (chromosome.isSelected()) {
                done = true;
            }
        }

        return parent;
    }

    public int chooseParent(int parentA) {
        int parent = 0;
        Chromosome chromosome;
        boolean done = false;

        while (!done) {
            parent = getRandomNumber(0, population.size() - 1);
            if (parent != parentA) {
                chromosome = population.get(parent);
                if (chromosome.isSelected()) {
                    done = true;
                }
            }
        }

        return parent;
    }

    public void rouletteSelection() {
        int j;
        int populationSize = population.size();
        int maximumToSelect = getRandomNumber(MIN_SELECT, MAX_SELECT);
        double genTotal = 0.0;
        double selTotal;
        double rouletteSpin;
        Chromosome firstChromosome;
        Chromosome secondChromosome;
        boolean done;

        for (Chromosome chromosome : population) {
            firstChromosome = chromosome;
            genTotal += firstChromosome.getFitness();
        }

        genTotal *= 0.01;

        for (Chromosome chromosome : population) {
            firstChromosome = chromosome;
            firstChromosome.setSelectionProbability(firstChromosome.getFitness() / genTotal);
        }

        for (int i = 0; i < maximumToSelect; i++) {
            rouletteSpin = getRandomNumber(0, 99);
            j = 0;
            selTotal = 0;
            done = false;
            while (!done) {
                firstChromosome = population.get(j);
                selTotal += firstChromosome.getSelectionProbability();
                if (selTotal >= rouletteSpin) {
                    if (j == 0) {
                        secondChromosome = population.get(j);
                    } else if (j >= populationSize - 1) {
                        secondChromosome = population.get(populationSize - 1);
                    } else {
                        secondChromosome = population.get(j - 1);
                    }
                    secondChromosome.setSelected(true);
                    done = true;
                } else {
                    j++;
                }
            }
        }
    }

    public void getFitness() {
        Chromosome chromosome1;
        double bestScore;
        double worstScore;

        worstScore = Collections.max(population).getConflicts();
        bestScore = worstScore - Collections.min(population).getConflicts();

        for (Chromosome chromosome : population) {
            chromosome1 = chromosome;
            chromosome1.setFitness((worstScore - chromosome1.getConflicts()) * 100.0 / bestScore);
        }
    }

    public void prepNextGeneration() {
        int populationSize;
        Chromosome chromosome;

        populationSize = population.size();
        for (int i = 0; i < populationSize; i++) {
            chromosome = population.get(i);
            chromosome.setSelected(false);
        }
    }

    public void printSolution(Chromosome solution) {
        String[][] board = new String[MAX_LENGTH][MAX_LENGTH];

        for (int x = 0; x < MAX_LENGTH; x++) {
            for (int y = 0; y < MAX_LENGTH; y++) {
                board[x][y] = "";
            }
        }

        for (int x = 0; x < MAX_LENGTH; x++) {
            board[x][solution.getGene(x)] = "Q";
        }

        System.out.println("Board:");
        for (int y = 0; y < MAX_LENGTH; y++) {
            for (int x = 0; x < MAX_LENGTH; x++) {
                if (Objects.equals(board[y][x], "Q")) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.print("\n");
        }
    }

    public void initialize() {
        int shuffles;
        Chromosome newChromosome;
        int chromosomeIndex;

        for (int i = 0; i < START_SIZE; i++) {
            newChromosome = new Chromosome(MAX_LENGTH);
            population.add(newChromosome);
            chromosomeIndex = population.indexOf(newChromosome);

            shuffles = getRandomNumber(MINIMUM_SHUFFLES, MAXIMUM_SHUFFLES);
            exchangeMutation(chromosomeIndex, shuffles);
            population.get(chromosomeIndex).computeConflicts();
        }
    }

    public void exchangeMutation(int index, int exchanges) {
        int tempData;
        int gene1;
        int gene2;
        Chromosome chromosome;
        chromosome = population.get(index);

        for (int i = 0; i < exchanges; i++) {
            gene1 = getRandomNumber(0, MAX_LENGTH - 1);
            gene2 = getExclusiveRandomNumber(MAX_LENGTH - 1, gene1);

            tempData = chromosome.getGene(gene1);
            chromosome.setGene(gene1, chromosome.getGene(gene2));
            chromosome.setGene(gene2, tempData);
        }
        mutations++;
    }

    public int getExclusiveRandomNumber(int high, int except) {
        boolean done = false;
        int getRand = 0;

        while (!done) {
            getRand = rand.nextInt(high);
            if (getRand != except) {
                done = true;
            }
        }
        return getRand;
    }

    public int getRandomNumber(int low, int high) {
        return (int) Math.round((high - low) * rand.nextDouble() + low);
    }

    public ArrayList<Chromosome> getSolutions() {
        return solutions;
    }

    public int getGeneration() {
        return generation;
    }

    public int getPopSize() {
        return population.size();
    }

    public int getStartSize() {
        return START_SIZE;
    }

    public double getMatingProb() {
        return MATING_PROBABILITY;
    }

    public double getMutationRate() {
        return MUTATION_RATE;
    }

    public int getMinSelect() {
        return MIN_SELECT;
    }

    public double getMaxSelect() {
        return MAX_SELECT;
    }

    public double getOffspring() {
        return OFFSPRING_PER_GENERATION;
    }

    public int getMaxGeneration() {
        return MAX_GENERATION;
    }

    public int getShuffleMin() {
        return MINIMUM_SHUFFLES;
    }

    public int getShuffleMax() {
        return MAXIMUM_SHUFFLES;
    }

    public void setMutation(double newMutation) {
        this.MUTATION_RATE = newMutation;
    }

    public void setGeneration(int newMaxGeneration) {
        this.MAX_GENERATION = newMaxGeneration;
    }

    public int getSTART_SIZE() {
        return START_SIZE;
    }

    public void setSTART_SIZE(int START_SIZE) {
        this.START_SIZE = START_SIZE;
    }
}