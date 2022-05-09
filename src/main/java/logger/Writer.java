package logger;
import model.Chromosome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Writer {
	private final ArrayList<String> list;
	public Writer() {
		list = new ArrayList<>();
	}
	public void add(String line) {
		list.add(line);
	}
	public void add(Chromosome c) {
		int n = c.getMaxLength();
		String[][] board = new String[n][n];

		clearBoard(board, n);

		for(int x = 0; x < n; x++) {
			board[x][c.getGene(x)] = "Q";
		}

		printBoard(board, n);
	}

	public void clearBoard(String[][] board, int n) {
		for(int x = 0; x < n; x++) {
			for(int y = 0; y < n; y++) {
				board[x][y] = "";
			}
		}
	}
	public void printBoard(String[][] board, int n) {
		for(int y = 0; y < n; y++) {
			StringBuilder temp = new StringBuilder();
			for(int x = 0; x < n; x++) {
				if(Objects.equals(board[x][y], "Q")) {
					temp.append("Q ");
				} else {
					temp.append(". ");
				}
			}
			list.add(temp.toString());
		}
	}
	public void writeFile(String filename) {
		try{
        	FileWriter fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String s : list) {
				bw.write(s);
				bw.newLine();
				bw.flush();
			}

			bw.close();
        } catch (IOException e) {
			e.printStackTrace();
        	System.out.println("Writing failed");
        }
		
	}
}
