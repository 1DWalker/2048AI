import java.lang.System;
import java.util.Random;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GameBase {
	
	static Random randomNum = new Random();
	
	static int[] options = new int[3];
	
	static byte[][] board = new byte[4][4];
	
	static int score = 0;
	
	static long totalScore = 0;
	static long trials = 0;
	
	static JFrame frame = new JFrame("2048");
	static JPanel mainPanel = new JPanel();
	static JPanel numberPanel = new JPanel();
	static JPanel textPanel = new JPanel();

	static JLabel scoreLabel = new JLabel();
	static JLabel textLabel = new JLabel();
	
	static JButton newGame = new JButton("New Game");
	
	static JLabel[][] numbers;
	
	public static void main(String[] args) {
		//Width of game
		options[0] = 4;
		//Height of game
		options[1] = 4; 
		//OutputGUI
		options[2] = 1;
		
		numbers = new JLabel[options[0]][options[1]];
		
		resetBoard();
		
		for (int col = 0; col < options[0]; col++) {
			for (int row = 0; row < options[1]; row++) {
				numbers[col][row] = new JLabel();
			}
		}
		
		if (options[2] == 1) {
			GUI();
		} else {
			//Auto-play games
		}
	}
	
	public static void GUI() {
		KeyListener listener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				int keyCode = event.getKeyCode();
				
				switch (keyCode) {
					case KeyEvent.VK_UP:
						changeBoard(0);
						setLabels();
						totalScore += score;
						break;
					case KeyEvent.VK_LEFT: 
						changeBoard(1);
						setLabels();
						totalScore += score;
						break;
					case KeyEvent.VK_DOWN:
						changeBoard(2);
						setLabels();
						totalScore += score;
						break;
					case KeyEvent.VK_RIGHT:
						changeBoard(3);
						setLabels();
						totalScore += score;
						break;
				}
				
				if (terminalBoard()) {
					textLabel.setText("Game Over!");
				}
			}
			
			public void keyReleased(KeyEvent event) {	
			}
			
			public void keyTyped(KeyEvent event) {
			}
		};
		
		setLabels();
		
		numberPanel.setBackground(new Color(184, 173, 161));
		numberPanel.setSize(new Dimension(400, 400));
		numberPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		numberPanel.setLayout(new GridLayout(4, 4, 8, 8));
		
		for (int row = 0; row < options[1]; row++) {
			for (int col = 0; col < options[0]; col++) {
				numbers[col][row].setOpaque(true);
				numbers[col][row].setHorizontalAlignment(SwingConstants.CENTER);
//				numbers[col][row].setMinimumSize(new Dimension(100, 100));
				numbers[col][row].setPreferredSize(new Dimension(100, 100));
				numberPanel.add(numbers[col][row]);
			}
		}
		
		newGame.setActionCommand("New Game");
		newGame.addActionListener(new Buttons());
		newGame.setBackground(new Color(184, 173, 161));
		newGame.setForeground(Color.white);
		
		scoreLabel.setOpaque(true);
		scoreLabel.setForeground(Color.white);
		scoreLabel.setBackground((new Color(184, 173, 161)));
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		textLabel.setBackground(new Color(251, 248, 240));
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		textPanel.setSize(new Dimension(400, 0));
		textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		textPanel.setLayout(new GridLayout(3, 1, 10, 10));
		textPanel.setBackground(new Color(251, 248, 240));
		textPanel.add(newGame);
		textPanel.add(scoreLabel);
		textPanel.add(textLabel);
		
		newGame.setFocusable(false);
		
		mainPanel.setSize(600, 600);
		mainPanel.setBackground(new Color(251, 248, 240));
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new FlowLayout());
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		mainPanel.add(textPanel);
		mainPanel.add(numberPanel);
		
		frame.addKeyListener(listener);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(mainPanel);
//		frame.pack();
		frame.setVisible(true);
//		frame.requestFocus();
	}
	
	private static class Buttons implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			resetBoard();
			setLabels();
		}
	}
	
	public static void setLabels() {
		int[][] boardInt = new int[board.length][board[0].length];
		for (int col = 0; col < board.length; col++) {
			for (int row = 0; row < board[0].length; row++) {
				if (board[col][row] == 0) {
					boardInt[col][row] = 0;
				} else {
					boardInt[col][row] = (int) Math.pow(2, board[col][row]);
				}
			}
		}
		
		for (int col = 0; col < options[0]; col++) {
			for (int row = 0; row < options[1]; row++) {
				if (board[col][row] != 0) {
					numbers[col][row].setText(Integer.toString(boardInt[col][row]));
				} else {
					numbers[col][row].setText("");
				}
				numbers[col][row].setFont(new Font("serif", Font.BOLD, 24));
				if (boardInt[col][row] == 2 | boardInt[col][row] == 4) {
					numbers[col][row].setForeground(new Color(121, 110, 102));
				} else {
					numbers[col][row].setForeground(Color.white);
				}
				
				switch ((int) board[col][row]) {
					case 0: //No tile
						numbers[col][row].setBackground(new Color(206, 192, 180));
						break;
					case 1: //2
						numbers[col][row].setBackground(new Color(239, 228, 218));
						break;
					case 2: //4
						numbers[col][row].setBackground(new Color(237, 223, 201));
						break;
					case 3: //8
						numbers[col][row].setBackground(new Color(243, 176, 122));
						break;
					case 4: //16
						numbers[col][row].setBackground(new Color(246, 148, 100));
						break;
					case 5: //32
						numbers[col][row].setBackground(new Color(238, 206, 114));
						break;
					case 6: //64
						numbers[col][row].setBackground(new Color(231, 122, 86));
						break;
					case 7: //128
						numbers[col][row].setBackground(new Color(237, 214, 142));
						break;
					case 8: //256
						numbers[col][row].setBackground(new Color(241, 204, 100));
						break;
					case 9: //512
						numbers[col][row].setBackground(new Color(237, 207, 113));
						break;
					case 10: //1012
						numbers[col][row].setBackground(new Color(240, 199, 72));
						break;
					case 11: //2048
						numbers[col][row].setBackground(new Color(240, 193, 47));
						break;
					default: //Higher than 2048
						numbers[col][row].setBackground(new Color(61, 57, 52));
						break;
				}
			}
		}
		
		scoreLabel.setText("Score: " + Integer.toString(score));
	}
	
	public static void playGame() {
		resetBoard();
		score = 0;
		if (options[2] == 1) printBoard();
		if (options[2] == 1) System.out.println();
		
		while (!terminalBoard()) {
			Random randomNum = new Random();
			int direction = randomNum.nextInt(4);
			String[] directionList = {"Up", "Left", "Down", "Right"};
			if (options[2] == 1) System.out.println(directionList[direction]);
			changeBoard(direction);
			if (options[2] == 1) printBoard();
			if (options[2] == 1) System.out.println();
			//break;
		}
		
		
		
//		System.out.println(score);
//		
//		int largestValue = 0;
//		for (int row = 0; row < options[1]; row++) {
//			for (int col = 0; col < options[0]; col++) {
//				if (Math.pow(2, board[col][row]) > largestValue) {
//					largestValue = (int) Math.pow(2, board[col][row]);
//				}
//			}
//		}
//		System.out.println("Largest tile: " + largestValue);
//		totalScore += score;
//		trials++;
	}
	
	public static void printBoard() {
		System.out.println("Score: " + score);
		
		int largestValue = 0;
		for (int row = 0; row < options[1]; row++) {
			for (int col = 0; col < options[0]; col++) {
				if (Math.pow(2, board[col][row]) > largestValue) {
					largestValue = (int) Math.pow(2, board[col][row]);
				}
			}
		}
		
		for (int row = 0; row < options[0]; row++) {
			for (int col = 0; col < options[1]; col++) {
				if (board[col][row] == 0) {
					for (int i = 0; i < Math.log10(largestValue) + 1; i++) {
						System.out.print(" ");
					}
				} else {
					for (int i = 0; i < (int) Math.log10(largestValue) - (int) Math.log10(Math.pow(2, board[col][row])); i++) {
						System.out.print(" ");
					}
				}
				
				System.out.print((int) Math.pow(2, board[col][row]) + " ");
			}
			
			System.out.println();
		}
	}
	
	public static void resetBoard() {
		for (int col = 0; col < options[0]; col++) {
			for (int row = 0; row < options[1]; row++) {
				board[col][row] = 0;
			}
		}
		
		//NOW ADD 2 TILES 
		for (int i = 0; i < 2; i++) {
			byte newTileValue = randomNum.nextDouble() < 0.9 ? (byte) 1 : (byte) 2;
			int newTileLocation = randomNum.nextInt(options[0] * options[1]);
			
			if (board[newTileLocation / options[1]][newTileLocation % options[1]] == 0) {
				board[newTileLocation / options[1]][newTileLocation % options[1]] = newTileValue;
			} else {
				i--;
				continue;
			}
		}
	}
	
	public static boolean changeBoard(int direction) {
		byte lastNum;
		int lastDimension;
		int reader;
		
		boolean change = false;
		if (direction == 0) { //up
			for (int col = 0; col < options[0]; col++) {
				lastNum = board[col][0];
				lastDimension = 0;
				
				//combine
				for (int row = 1; row < options[1]; row++) {
					if (board[col][row] == 0) continue;
					if (board[col][row] == lastNum){
						board[col][row]++;
						score += Math.pow(2, board[col][row]);
						board[col][lastDimension] = 0;
						row++;
						change = true;
						if (row >= options[1]) break;
					}
					
					lastNum = board[col][row];
					lastDimension = row;
				}
				
				//squish
				reader = 1;
				squish:
				for (int writer = 0; writer < options[1]; writer++) {
					if (board[col][writer] == 0) {
						while (reader < options[1]) {
							if (board[col][reader] != 0) break;
							reader++;
						}
						if (reader == options[1]) break squish;
						
						board[col][writer] = board[col][reader];
						board[col][reader] = 0;
						change = true;
					} else {
						reader++;
					}
				}	
			}
		} else if (direction == 1) { //left
			for (int row = 0; row < options[1]; row++) {
				lastNum = board[0][row];
				lastDimension = 0;
				
				//combine
				for (int col = 1; col < options[0]; col++) {
					if (board[col][row] == 0) continue;
					if (board[col][row] == lastNum){
						board[col][row]++;
						score += Math.pow(2, board[col][row]);
						board[lastDimension][row] = 0;
						col++;
						change = true;
						
						if (col >= options[0]) break;
					}
					
					lastNum = board[col][row];
					lastDimension = col;
				}

				reader = 1;
				squish: 
				for (int writer = 0; writer < options[0]; writer++) {
					if (board[writer][row] == 0) {
						while (reader < options[0]) {
							if (board[reader][row] != 0) break;
							reader++;
						} 
						if (reader == options[0]) break squish;
						
						board[writer][row] = board[reader][row];
						board[reader][row] = 0;
						change = true;
					} else {
						reader++;
					}
				}	
			}
		} else if (direction == 2) { //down
			for (int col = 0; col < options[0]; col++) {
				lastNum = board[col][options[1] - 1];
				lastDimension = options[1] - 1;
				
				//combine
				for (int row = options[1] - 2; row >= 0; row--) {
					if (board[col][row] == 0) continue;
					if (board[col][row] == lastNum){
						board[col][row]++;
						score += Math.pow(2, board[col][row]);
						board[col][lastDimension] = 0;
						row--;
						change = true;
						
						if (row < 0) break;
					}
					
					lastNum = board[col][row];
					lastDimension = row;
				}
				
				//squish
				reader = options[1] - 2;
				squish:
				for (int writer = options[1] - 1; writer >= 0; writer--) {
					if (board[col][writer] == 0) {
						while (reader >= 0) {
							if (board[col][reader] != 0) {
								break;
							}
							reader--;
						}
						if (reader == -1) break squish;
						
						board[col][writer] = board[col][reader];
						board[col][reader] = 0;
						change = true;
					} else {
						reader--;
					}
				}	
			}
		} else if (direction == 3) { //right
			for (int row = 0; row < options[1]; row++) {
				lastNum = board[options[0] - 1][row];
				lastDimension = options[0] - 1;
				
				//combine
				for (int col = options[0] - 2; col >= 0; col--) {
					if (board[col][row] == 0) continue;
					if (board[col][row] == lastNum){
						board[col][row]++;
						score += Math.pow(2, board[col][row]);
						board[lastDimension][row] = 0;
						col--;
						change = true;
						
						if (col < 0) break;
					}
					
					lastNum = board[col][row];
					lastDimension = col;
				}
				
				//squish
				reader = options[0] - 2;
				squish:
				for (int writer = options[0] - 1; writer >= 0; writer--) {
					if (board[writer][row] == 0) {
						while (reader >= 0) {
							if (board[reader][row] != 0) break;
							reader--;
						}
						if (reader == -1) break squish;
						
						board[writer][row] = board[reader][row];
						board[reader][row] = 0;
						change = true;
					} else {
						reader--;
					}
				}	
			}
		}

		if (change) {
			while (true) {
				byte newTileValue = randomNum.nextDouble() < 0.9 ? (byte) 1 : (byte) 2;
				int newTileLocation = randomNum.nextInt(options[0] * options[1]);
				
				if (board[newTileLocation / options[1]][newTileLocation % options[1]] == 0) {
					board[newTileLocation / options[1]][newTileLocation % options[1]] = newTileValue;
				} else {
					continue;
				}
				
				break;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean terminalBoard() {
		byte lastNum = 0;
		
		for (int col = 0; col < options[0]; col++) {
			lastNum = 0;
			
			for (int row = 0; row < options[1]; row++) {
				if (board[col][row] == 0) return false;
				if (board[col][row] == lastNum) return false;
				else lastNum = board[col][row];
			}
		}
		
		for (int row = 0; row < options[1]; row++) {
			lastNum = 0;
			
			for (int col = 0; col < options[0]; col++) {
				if (board[col][row] == 0) return false;
				if (board[col][row] == lastNum) return false;
				else lastNum = board[col][row];
			}
		}
		
		return true;
	}

}


