import java.util.Random;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.FileReader;
import java.io.BufferedReader;

public class NeuralNetworkTrainer {
	static Random randomNum = new Random();
	
	static double highScore = 0;
	
	static double bias = 1;
	static double[][] inputWeightsHL1 = new double[17][2];
	static double[][] inputWeightsHL2 = new double[2][2];
	static double[][] inputWeightsHL3 = new double[2][2];
	static double[][] inputWeightsOutput = new double[2][4];
	
	//Reinforement learning. CE stands for characteristic elegibility
	static double[][] CEinputWeightsHL1 = new double[17][2];
	static double[][] CEinputWeightsHL2 = new double[2][2];
	static double[][] CEinputWeightsHL3 = new double[2][2];
	static double[][] CEinputWeightsOutput = new double[2][4];
	
	
	static double learningRateInput = 0.0000000001;
	static double learningRateHidden = 0.0000000001;
	static double learningRateOutput = 0.0000000001;
	
	static double baseline = 0;
	
	static int numberOfChanges = 0;
	static boolean weights = false;
	
	public static int manager(byte[][] board, int[] options) {
		if (weights == false) {
			setCE();
			readWeights();
			weights = true;
		}
		
//		return randomNum.nextInt(4);
		return chooseDirection(board, options);
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
		
		for (int i = 0; i < inputWeightsHL3.length; i++) {
			for (int k = 0; k < inputWeightsHL3[0].length; k++) {
				inputWeightsHL3[i][k] = randomNum.nextGaussian();
			}
		}
		
		for (int i = 0; i < inputWeightsOutput.length; i++) {
			for (int k = 0; k < inputWeightsOutput[0].length; k++) {
				inputWeightsOutput[i][k] = randomNum.nextGaussian();
			}
		}
	}
	
	public static void changeWeights(double reinforcementValue) {
		double averageChange = 0;
		
		for (int i = 0; i < inputWeightsHL1.length; i++) {
			for (int k = 0; k < inputWeightsHL1[0].length; k++) {
//				System.out.println( learningRate * (reinforcementValue - baseline) * CEinputWeightsHL1[i][k]);
				inputWeightsHL1[i][k] += learningRateInput * (reinforcementValue - baseline) * CEinputWeightsHL1[i][k];
//				System.out.println(learningRateInput * (reinforcementValue - baseline) * CEinputWeightsHL1[i][k]);
				averageChange += learningRateInput * (reinforcementValue - baseline) * CEinputWeightsHL1[i][k];
			}
		}
		
		for (int i = 0; i < inputWeightsHL2.length; i++) {
			for (int k = 0; k < inputWeightsHL2[0].length; k++) {
				inputWeightsHL2[i][k] += learningRateHidden * (reinforcementValue - baseline) * CEinputWeightsHL2[i][k];
//				System.out.println(learningRateHidden * (reinforcementValue - baseline) * CEinputWeightsHL2[i][k]);
				averageChange += learningRateHidden * (reinforcementValue - baseline) * CEinputWeightsHL2[i][k];
			}
		}
		
//		System.exit(1);
		
		for (int i = 0; i < inputWeightsHL3.length; i++) {
			for (int k = 0; k < inputWeightsHL3[0].length; k++) {
				inputWeightsHL3[i][k] += learningRateHidden * (reinforcementValue - baseline) * CEinputWeightsHL3[i][k];
				averageChange += learningRateHidden * (reinforcementValue - baseline) * CEinputWeightsHL3[i][k];
			}
		}
		
		for (int i = 0; i < inputWeightsOutput.length; i++) {
			for (int k = 0; k < inputWeightsOutput[0].length; k++) {
//				System.out.println(inputWeightsOutput[i][k] + "   " + (learningRate * (reinforcementValue - baseline) * CEinputWeightsOutput[i][k]));
				inputWeightsOutput[i][k] += learningRateOutput * (reinforcementValue - baseline) * CEinputWeightsOutput[i][k];
				averageChange += learningRateOutput * (reinforcementValue - baseline) * CEinputWeightsOutput[i][k];
			}
		}
		
		System.out.println("Summed weight change: " + averageChange);
		System.out.println();
		numberOfChanges++;
		
//		baseline = 0.01 * reinforcementValue + (1 - 0.01) * baseline;
		
		if (numberOfChanges % 1000000 == 0) {
			learningRateInput /= 2.0;
			learningRateHidden /= 2.0;
			learningRateOutput /= 2.0;
		}
//		System.exit(1);
	}
		
	public static int chooseDirection(byte[][] board, int[] options) {
		//SIGMOID ACTIVATION FUNCTION 1(1+e^-x)
		
		double bestOutput = 0;
		double randomAddition = 0;
		
		//Standardize input layer
		
		double[] nodesInput = new double[17];
		int counter = 0;
		for (int col = 0; col < board.length; col++) {
			for (int row = 0; row < board[0].length; row++) {
				nodesInput[counter] = (double) board[col][row] / 17.0;
				counter++;
			}
		}
		
		nodesInput[16] = bias; //BIAS NODE
		
		//1st hidden layer
		double[] nodesHL1 = new double[inputWeightsHL1[0].length];
		for (int node = 0; node < nodesHL1.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < nodesInput.length; i++) {
				sum += nodesInput[i] * inputWeightsHL1[i][node];
			}
			
			randomAddition = randomDistribution() / 5;
			bestOutput = 1 / (1 + Math.exp(-sum));
//			nodesHL1[node] = 1 / (1 + Math.exp(-sum)) + randomAddition;
			nodesHL1[node] = 1 / (1 + Math.exp(-sum + randomAddition));
			
			for (int i = 0; i < nodesInput.length; i++) {
				CEinputWeightsHL1[i][node] += (nodesHL1[node] - bestOutput) * nodesInput[i];
			}
		}
		
		//2nd hidden layer
		double[] nodesHL2 = new double[inputWeightsHL2[0].length];
		for (int node = 0; node < nodesHL2.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < nodesHL1.length; i++) {
				sum += nodesHL1[i] * inputWeightsHL2[i][node];
			}
			
			randomAddition = randomDistribution() / 5;
			bestOutput = 1 / (1 + Math.exp(-sum));
//			nodesHL2[node] = 1 / (1 + Math.exp(-sum)) + randomAddition;
			nodesHL2[node] = 1 / (1 + Math.exp(-sum + randomAddition));

			
			for (int i = 0; i < nodesHL1.length; i++) {
				CEinputWeightsHL2[i][node] += (nodesHL2[node] - bestOutput) * nodesHL1[i];
			}
		}
		
		//3rd hidden layer
		double[] nodesHL3 = new double[inputWeightsHL3[0].length];
		for (int node = 0; node < nodesHL3.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < nodesHL2.length; i++) {
				sum += nodesHL2[i] * inputWeightsHL3[i][node];
			}
			
			randomAddition = randomDistribution() / 5;
			bestOutput = 1 / (1 + Math.exp(-sum));
//			nodesHL3[node] = 1 / (1 + Math.exp(-sum)) + randomAddition;
			nodesHL3[node] = 1 / (1 + Math.exp(-sum + randomAddition));

			
			for (int i = 0; i < nodesHL2.length; i++) {
				CEinputWeightsHL3[i][node] += (nodesHL3[node] - bestOutput) * nodesHL2[i];
			}
		}
		
		//Output layer
		double[] nodesOutput = new double[inputWeightsOutput[0].length];
		for (int node = 0; node < nodesOutput.length; node++) {
			//Weighted sum
			double sum = 0;
			for (int i = 0; i < nodesHL3.length; i++) {
				sum += nodesHL3[i] * inputWeightsOutput[i][node];
			}
			
			randomAddition = randomDistribution() / 5;
			bestOutput = 1 / (1 + Math.exp(-sum));
//			nodesOutput[node] = 1 / (1 + Math.exp(-sum)) + randomAddition;
			nodesOutput[node] = 1 / (1 + Math.exp(-sum + randomAddition));
			
			for (int i = 0; i < nodesHL3.length; i++) {
				CEinputWeightsOutput[i][node] += (nodesOutput[node] - bestOutput) * nodesHL3[i];
			}
		}
		
		//Softmax
		double sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += nodesOutput[i];
		}
		
		for (int i = 0; i < 4; i++) {
			nodesOutput[i] = nodesOutput[i] / sum;
		}
		
		if (sum != 0) {
			while (true) {
				int bestDirection = 0;
				double bestScore = nodesOutput[0];
				for (int i = 0; i < 4; i++) {
					if (nodesOutput[i] > bestScore) {
						bestScore = nodesOutput[i];
						bestDirection = i;
					}
				}
						
				if (terminal(board, bestDirection)) {
					nodesOutput[bestDirection] = -1;
					continue;
				} else {
					return bestDirection;
				}
			}	
		} else {
			for (int i = 0; i < 4; i++) {
				System.out.println("Sum is 0");
				System.exit(1);
				if (!terminal(board, i)) return i;
			}
		}
		
//		while (true) {
//			int bestDirection = -1;
//			
//			double probability = randomNum.nextDouble();
//			for (int i = 0; i < 4; i++) {
//				if (nodesOutput[i] > probability) {
//					bestDirection = i;
//					break;
//				} else {
//					probability -= nodesOutput[i];
//				}
//			}
//			
//			if (terminal(board, bestDirection)) {
//				continue;
//			} else {
//				return bestDirection;
//			}
//		}
		return 0;
	}
	
	public static double randomDistribution() {
		return randomNum.nextGaussian();
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
			File HiddenWeights = new File("HiddenWeights.txt");
			File HL2ToOutputWeights = new File("HL2ToOutputWeights.txt");
			
			FileWriter InputHL1Stream = new FileWriter(InputToHL1Weights, false);
			for (int node = 0; node < inputWeightsHL1[0].length; node++) {
				for (int input = 0; input < inputWeightsHL1.length; input++) {
					InputHL1Stream.write(Double.toString(inputWeightsHL1[input][node]));
					InputHL1Stream.write(System.getProperty("line.separator"));
				}
			}
			InputHL1Stream.close();	

			FileWriter HiddenStream = new FileWriter(HiddenWeights, false);
			for (int node = 0; node < inputWeightsHL2[0].length; node++) {
				for (int input = 0; input < inputWeightsHL2.length; input++) {
					HiddenStream.write(Double.toString(inputWeightsHL2[input][node]));
					HiddenStream.write(System.getProperty("line.separator"));
				}
			}
			
			for (int node = 0; node < inputWeightsHL3[0].length; node++) {
				for (int input = 0; input < inputWeightsHL3.length; input++) {
					HiddenStream.write(Double.toString(inputWeightsHL3[input][node]));
					HiddenStream.write(System.getProperty("line.separator"));
				}
			}
			HiddenStream.close();
			
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
			
			BufferedReader HiddenReader = new BufferedReader(new FileReader("HiddenWeights.txt"));
			for (int node = 0; node < inputWeightsHL2[0].length; node++) {
				for (int input = 0; input < inputWeightsHL2.length; input++) {
					inputWeightsHL2[input][node] = Double.parseDouble(HiddenReader.readLine());
				}
			}
			
			for (int node = 0; node < inputWeightsHL3[0].length; node++) {
				for (int input = 0; input < inputWeightsHL3.length; input++) {
					inputWeightsHL3[input][node] = Double.parseDouble(HiddenReader.readLine());
				}
			}
			HiddenReader.close();	
			
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
	
	public static void setCE() {
		for (int i = 0; i < CEinputWeightsHL1.length; i++) {
			for (int k = 0; k < CEinputWeightsHL1[0].length; k++) {
				CEinputWeightsHL1[i][k] = 0;
			}
		}
		
		for (int i = 0; i < CEinputWeightsHL2.length; i++) {
			for (int k = 0; k < CEinputWeightsHL2[0].length; k++) {
				CEinputWeightsHL2[i][k] = 0;
			}
		}
		
		for (int i = 0; i < CEinputWeightsHL2.length; i++) {
			for (int k = 0; k < CEinputWeightsHL2[0].length; k++) {
				CEinputWeightsHL3[i][k] = 0;
			}
		}
		
		for (int i = 0; i < CEinputWeightsOutput.length; i++) {
			for (int k = 0; k < CEinputWeightsOutput[0].length; k++) {
				CEinputWeightsOutput[i][k] = 0;
			}
		}
	}
}