package model;


import java.util.Arrays;

public class Chromosome implements Comparable<Chromosome>{
	private final int MAX_LENGTH;
	private final int[] gene;
	private double fitness;
	private int conflicts;
	private boolean selected;
	private double selectionProbability;

	public Chromosome(int n) {
		MAX_LENGTH = n;
		gene = new int[MAX_LENGTH];
		fitness = 0.0;
		conflicts = 0;
		selected = false;
		selectionProbability = 0.0;
		
		initChromosome();
	}
	public int compareTo(Chromosome c) {
		return this.conflicts - c.getConflicts();
	}
	public void computeConflicts() {
		String[][] board = new String[MAX_LENGTH][MAX_LENGTH];
		int x;
        int y;
        int tempx;
        int tempy;
        
        int[] dx = new int[] {-1, 1, -1, 1};
        int[] dy = new int[] {-1, 1, 1, -1};
        
        boolean done;
        int conflicts = 0;
        
        clearBoard(board);
        plotQueens(board);

        for(int i = 0; i < MAX_LENGTH; i++) {
            x = i;
            y = gene[i];


            for(int j = 0; j < 4; j++) {
                tempx = x;
                tempy = y;
                done = false;
                
                while(!done) {
                    tempx += dx[j];
                    tempy += dy[j];
                    
                    if((tempx < 0 || tempx >= MAX_LENGTH) || (tempy < 0 || tempy >= MAX_LENGTH)) {
                        done = true;
                    } else {
                        if(board[tempx][tempy].equals("Q")) {
                            conflicts++;
                        }
                    }
                }
            }
        }

        this.conflicts = conflicts;
        
	}
	public void plotQueens(String[][] board) {
        for(int i = 0; i < MAX_LENGTH; i++) {
            board[i][gene[i]] = "Q";
        }
	}
	public void clearBoard(String[][] board) {
		for (int i = 0; i < MAX_LENGTH; i++) {
			for (int j = 0; j < MAX_LENGTH; j++) {
				board[i][j] = "";
			}
		}
	}
	public void initChromosome() {
		for(int i = 0; i < MAX_LENGTH; i++) {
			gene[i] = i;
		}
	}
	public int getGene(int index) {
		return gene[index];
	}
	public void setGene(int index, int position) {
		this.gene[index] = position;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	public int getConflicts() {
		return conflicts;
	}
	public void setConflicts(int conflicts) {
		this.conflicts = conflicts;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public double getSelectionProbability() {
		return selectionProbability;
	}
	public void setSelectionProbability(double selectionProbability) {
		this.selectionProbability = selectionProbability;
	}
	public int getMaxLength() {
	   return MAX_LENGTH;
	}

	@Override
	public String toString() {
		return "Chromosome{" +
				"gene=" + Arrays.toString(gene) +
				'}';
	}
}