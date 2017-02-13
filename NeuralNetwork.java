import java.util.Random;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

public class NeuralNetwork {
	static Random randomNum = new Random();
	
	static double highScore = 0;
	
	static double[][] inputWeightsHL1 = new double[16][16];
	static double[][] inputWeightsHL2 = new double[16][16];
	static double[][] inputWeightsOutput = new double[16][4];
	static boolean weights = false;
	
	static double weightChange = 1.0 / 5;
	
	public static int manager(byte[][] board) {
		if (weights == false) {
//			resetWeights();
			readWeights();
			weights = true;
		}
		
//		return randomNum.nextInt(4);
		return chooseDirection(board);
	}
	
	public static void resetWeights() {
		//randomize weights
		for (int i = 0; i < inputWeightsHL1.length; i++) {
			for (int k = 0; k < inputWeightsHL1[0].length; k++) {
				inputWeightsHL1[i][k] = randomNum.nextGaussian();
			}
		}
		
		for (int i = 0; i < inputWeightsHL2.length; i++) {
			for (int k = 0; k < inputWeightsHL2[0].length; k++) {
				inputWeightsHL2[i][k] = randomNum.nextGaussian();
			}
		}
		
		for (int i = 0; i < inputWeightsOutput.length; i++) {
			for (int k = 0; k < inputWeightsOutput[0].length; k++) {
				inputWeightsOutput[i][k] = randomNum.nextGaussian();
			}
		}
	}
	
	public static void changeWeights() {
		for (int i = 0; i < inputWeightsHL1.length; i++) {
			for (int k = 0; k < inputWeightsHL1[0].length; k++) {
				inputWeightsHL1[i][k] += randomNum.nextBoolean() ? (randomNum.nextBoolean() ? weightChange : -weightChange) : 0;
			}
		}
		
		for (int i = 0; i < inputWeightsHL2.length; i++) {
			for (int k = 0; k < inputWeightsHL2[0].length; k++) {
				inputWeightsHL2[i][k] += randomNum.nextBoolean() ? (randomNum.nextBoolean() ? weightChange : -weightChange) : 0;
			}
		}
		
		for (int i = 0; i < inputWeightsOutput.length; i++) {
			for (int k = 0; k < inputWeightsOutput[0].length; k++) {
				inputWeightsOutput[i][k] += randomNum.nextBoolean() ? (randomNum.nextBoolean() ? weightChange : -weightChange) : 0;
			}
		}
	}
	
	public static void decreaseWeightChange() {
		weightChange = weightChange / 2.0;
	}
	
	public static int chooseDirection(byte[][] board) {
		//SIGMOID ACTIVATION FUNCTION
		
		//Standardize input layer
		//Find highest number
		int largestNumber = 0;
		for (int col = 0; col < board.length; col++) {
			for (int row = 0; row < board[0].length; row++) {
				if(board[col][row] > largestNumber) largestNumber = board[col][row];
			}
		}
		
		double[] nodesInput = new double[16];
		int counter = 0;
		for (int col = 0; col < board.length; col++) {
			for (int row = 0; row < board[0].length; row++) {
				nodesInput[counter] = (double) board[col][row] / largestNumber;
				counter++;
			}
		}
		
		//1st hidden layer
		double[] nodesHL1 = new double[16];
		for (int node = 0; node < nodesHL1.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < 16; i++) {
				sum += nodesInput[i] * inputWeightsHL1[i][node];
			}
			
			nodesHL1[node] = 1 / (1 + Math.exp(-sum));
		}
		
		//2nd hidden layer
		double[] nodesHL2 = new double[16];
		for (int node = 0; node < nodesHL1.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < 16; i++) {
				sum += nodesHL1[i] * inputWeightsHL2[i][node];
			}
			
			nodesHL2[node] = 1 / (1 + Math.exp(-sum));
		}
		
		//Output layer
		double[] nodesOutput = new double[4];
		for (int node = 0; node < nodesOutput.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < 16; i++) {
				sum += nodesHL2[i] * inputWeightsOutput[i][node];
			}
			
			nodesOutput[node] = 1 / (1 + Math.exp(-sum));
//			System.out.println(nodesHL2[node]);
		}
		
		//Softmax
		double sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += nodesOutput[i];
		}
		
		for (int i = 0; i < 4; i++) {
			nodesOutput[i] = nodesOutput[i] / sum;
		}
		
		while (true) {
			int bestDirection = -1;
//			double bestScore = -1;
//			for (int i = 0; i < 4; i++) {
//				if (nodesOutput[i] > bestScore) {
//					bestScore = nodesOutput[i];
//					bestDirection = i;
//				}
//			}
			
			double probability = randomNum.nextDouble();
			for (int i = 0; i < 4; i++) {
				if (nodesOutput[i] > probability) {
					bestDirection = i;
					break;
				} else {
					probability -= nodesOutput[i];
				}
			}
			
			if (terminal(board, bestDirection)) {
//				nodesOutput[bestDirection] = -1;
				continue;
			} else {
				return bestDirection;
			}
		}
	}
	
	public static boolean terminal(byte board[][], int direction) {
		byte lastNum = 0;
		boolean emptyTile = false;
		if (direction == 0) {
			for (int col = 0; col < board.length; col++) {
				lastNum = -1;
				emptyTile = false;
			
				for (int row = 0; row < board[0].length; row++) {
					if (board[col][row] == 0) emptyTile = true;
					if (board[col][row] != 0 & emptyTile) return false;
					if (board[col][row] == lastNum) return false;
					if (board[col][row] != 0) lastNum = board[col][row];
				}
			}
			
		} else if (direction == 1) {
			for (int row = 0; row < board[0].length; row++) {
				lastNum = -1;
				emptyTile = false;
				
				for (int col = 0; col < board.length; col++) {
					if (board[col][row] == 0) emptyTile = true;
					if (board[col][row] != 0 & emptyTile) return false;
					if (board[col][row] == lastNum) return false;
					if (board[col][row] != 0) lastNum = board[col][row];
				}
			}
		} else if (direction == 2) {
			for (int col = 0; col < board.length; col++) {
				lastNum = -1;
				emptyTile = false;
			
				for (int row = board[0].length - 1; row >= 0; row--) {
					if (board[col][row] == 0) emptyTile = true;
					if (board[col][row] != 0 & emptyTile) return false;
					if (board[col][row] == lastNum) return false;
					if (board[col][row] != 0) lastNum = board[col][row];
				}
			}
		} else {
			for (int row = 0; row < board[0].length; row++) {
				lastNum = -1;
				emptyTile = false;
				
				for (int col = board.length - 1; col >= 0; col--) {
					if (board[col][row] == 0) emptyTile = true;
					if (board[col][row] != 0 & emptyTile) return false;
					if (board[col][row] == lastNum) return false;
					if (board[col][row] != 0) lastNum = board[col][row];
				}
			}
		}
		
		return true;
	}
	
	public static void writeWeights() {
		try {
			File InputToHL1Weights = new File("InputToHL1Weights.txt");
			File HL1ToHL2Weights = new File("HL1ToHL2Weights.txt");
			File HL2ToOutputWeights = new File("HL2ToOutputWeights.txt");
			
			FileWriter InputHL1Stream = new FileWriter(InputToHL1Weights, false);
			for (int node = 0; node < inputWeightsHL1[0].length; node++) {
				for (int input = 0; input < inputWeightsHL1.length; input++) {
					InputHL1Stream.write(Double.toString(inputWeightsHL1[input][node]));
					InputHL1Stream.write(System.getProperty("line.separator"));
				}
			}
			InputHL1Stream.close();	

			FileWriter HL1HL2Stream = new FileWriter(HL1ToHL2Weights, false);
			for (int node = 0; node < inputWeightsHL2[0].length; node++) {
				for (int input = 0; input < inputWeightsHL2.length; input++) {
					HL1HL2Stream.write(Double.toString(inputWeightsHL2[input][node]));
					HL1HL2Stream.write(System.getProperty("line.separator"));
				}
			}
			HL1HL2Stream.close();
			
			FileWriter HL2OutputStream = new FileWriter(HL2ToOutputWeights, false);
			for (int i = 0; i < 4; i++) {
				for (int input = 0; input < inputWeightsOutput.length; input++) {
					HL2OutputStream.write(Double.toString(inputWeightsOutput[input][i]));
					HL2OutputStream.write(System.getProperty("line.separator"));
				}
			}
			HL2OutputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found. " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("IOException. " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void readWeights() {
		try {			
			BufferedReader InputToHL1Reader = new BufferedReader(new FileReader("InputToHL1Weights.txt"));
			for (int node = 0; node < inputWeightsHL1[0].length; node++) {
				for (int input = 0; input < inputWeightsHL1.length; input++) {
					inputWeightsHL1[input][node] = Double.parseDouble(InputToHL1Reader.readLine());
				}
			}
			InputToHL1Reader.close();
			
			BufferedReader HL1ToHL2Reader = new BufferedReader(new FileReader("HL1ToHL2Weights.txt"));
			for (int node = 0; node < inputWeightsHL2[0].length; node++) {
				for (int input = 0; input < inputWeightsHL2.length; input++) {
					inputWeightsHL2[input][node] = Double.parseDouble(HL1ToHL2Reader.readLine());
				}
			}
			HL1ToHL2Reader.close();	
			
			BufferedReader HL2ToOutputReader = new BufferedReader(new FileReader("HL2ToOutputWeights.txt"));
			for (int node = 0; node < inputWeightsOutput[0].length; node++) {
				for (int input = 0; input < inputWeightsOutput.length; input++) {
					inputWeightsOutput[input][node] = Double.parseDouble(HL2ToOutputReader.readLine());
				}
			}
			HL2ToOutputReader.close();	
		} catch (FileNotFoundException e) {
			System.out.println("File not found. " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("IOException. " + e.getMessage());
			System.exit(1);
		} 
	}
}

















