/*	
 *	Name: Connect Four
 * 	Author: Mahdi Varposhti
 * 	Date: June 7, 2017
 * 
 */
package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Game {
	
	//Board Array with 7 columns and 6 rows
	private static int[][] board = new int[7][6]; //Value of: 0 -> empty, 1 -> Player 1 piece, 2 -> Player 2 piece
	static JLabel[][] circles = new JLabel[7][6]; //Visual Board
	
	static int playersTurn = 1;
	
	//The main frame of the program
	static JFrame frame = new JFrame();
	
	//Name of the players
	static String p1Name;
	static String p2Name;

	//Initial Score of each player
	private static int p1Score = 0;
	private static int p2Score = 0;
		
	//Score texts
	static JLabel p1ScoreText = new JLabel("" + p1Score);
	static JLabel p2ScoreText = new JLabel("" + p2Score);
	
	public static void main(String[] args) throws IOException {
		
		//Set the name of each player
		p1Name = JOptionPane.showInputDialog("Enter a name for player 1:");
		p2Name = JOptionPane.showInputDialog("Enter a name for player 2:");
		
		ActionListener listener = new ActionListener(){ //ActionListener for the 7 buttons
			public void actionPerformed(ActionEvent e){
				String selectedCol = ((JButton) e.getSource()).getText();
				//System.out.print(selectedRow);
				if(inputPiece(Integer.parseInt(selectedCol),playersTurn)){
					
					//Change turn
					if(playersTurn==1){
						playersTurn = 2;
					}else{
						playersTurn = 1;
					}
					
					int winner = hasWon(); //Check if any player has won, 0->no winner, 1->player 1 has won, 2->player 2 has won
					if(winner!=0){
						
						if(winner==1){
							//Update the score
							p1Score++;
							p1ScoreText.setText("" + p1Score);
							
							JOptionPane.showMessageDialog(null, p1Name + " Won!", "Winner!", JOptionPane.INFORMATION_MESSAGE); //Show dialog
						}else{
							//Update the score
							p2Score++;
							p2ScoreText.setText("" + p2Score);
							
							JOptionPane.showMessageDialog(null, p2Name + " Won!", "Winner!", JOptionPane.INFORMATION_MESSAGE); //Show dialog
						}
						
						resetBoard(); //Restart the game
						
					}else if(isTie()){
						JOptionPane.showMessageDialog(null,"Tie!", "Tie", JOptionPane.INFORMATION_MESSAGE);
						resetBoard();
					}
				}
			}
		};
		
		frame.setTitle("Connect Four");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(420,480));
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.decode("#0080ff"));
		frame.setLayout(new GridLayout(8,7));
		
		JButton[] buttons = new JButton[7]; //The 7 buttons at the top
		int btnText = 1;
		JPanel btnPanel;
		
		for(int i = 0; i<7; i++){ //Add the buttons to the frame
			buttons[i] = new JButton("" + btnText);
			buttons[i].setPreferredSize(new Dimension(45, 30));
			btnPanel = new JPanel(new GridBagLayout());
			btnPanel.setPreferredSize(new Dimension(60, 60));
			btnPanel.setBackground(Color.decode("#0080ff"));
			btnPanel.add(buttons[i]);
			buttons[i].addActionListener(listener);
			frame.add(btnPanel);
			btnText++;
		}
		
		for(int r=0; r<6; r++){
			for(int c=0; c<7; c++){
				//Add the circles to the frame
				circles[c][r] = new JLabel();
				circles[c][r].setIcon(new ImageIcon(Game.class.getResource("circle.png")));
				circles[c][r].setHorizontalAlignment(SwingConstants.CENTER); 
				frame.add(circles[c][r]);
				
			}
		}
		
		//Player name text container
		JLabel p1Text = new JLabel(p1Name + ":");
		JLabel p2Text = new JLabel(p2Name + ":");
		
		p1Text.setBorder(new EmptyBorder(0,10,0,0));
		p2Text.setBorder(new EmptyBorder(0,10,0,0));
		
		frame.add(p1Text);
		frame.add(p1ScoreText);
		frame.add(p2Text);
		frame.add(p2ScoreText);
		
		//Initialize the board array
		for(int br = 0; br<6; br++){
			for(int bc = 0; bc<7; bc++){
				board[bc][br] = 0; //Loop through array and set all values to 0
			}
		}
		
		frame.pack();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter(){ // Listener on window close
			public void windowClosing(WindowEvent e){
				try{
					File file = new File("game_data.txt"); //File source
					PrintWriter output = new PrintWriter(new FileOutputStream(file,true));
					output.println(p1Name + ": " + p1Score + "          " + p2Name + ": " + p2Score); // When the program is closed save the scores to the file
					output.println("===================================");
					output.close();
				}catch(IOException exception){
					System.out.println("File Not Found");
				}
			}
		});
	}
	
	//Method for inserting a new piece into the board
	public static boolean inputPiece(int col,int player){
		int c = col-1;
		int r;
		for(r = 0; r<6; r++){
			if(board[c][r]!=0){
				if(r==0){
					return false; //Return false if column is already filled
				}
				r--;
				break;
			}else if(r==5){ //Last row of the column
				break;
			}
		}
		board[c][r] = player; //Set the value of that position to the player's piece
		printBoard();
		boardInputPiece(c,r,player);
		return true;
	}
	
	public static void boardInputPiece(int col,int row,int player){
		String pieceImage;
		if(player == 1){
			pieceImage = "red.png"; //Player 1 piece image, red circle
		}else{
			pieceImage = "yellow.png"; //Player 2 piece image, yellow circle
		}
		circles[col][row].setIcon(new ImageIcon(Game.class.getResource(pieceImage)));
	}
	
	public static boolean checkConnected(int a,int b,int c,int d){
		if(a==b && a==c && a==d){ //If all 4 pieces are the same
			return true;
		}
		
		return false;
	}
	
	// Check the board to see if there are any 4 adjacent pieces of the same number
	public static int hasWon(){
		int r,c;
		
		for(r=0; r<6; r++){
			for(c=0; c<7; c++){
				if(board[c][r]!=0){ //If it's not an empty spot
					
					if(r<3){ //Only need to check the first 4 rows, also to prevent array out of bound error( board[c][r+3] when r>3; r+3 is out of bound)
						if( checkConnected(board[c][r], board[c][r+1], board[c][r+2], board[c][r+3]) ){ //Check each column, if 4 pieces are connected vertically
							return board[c][r]; //Return the winning player
						}
					}
					
					if(c<4){ //Only need to check the first 4 columns
						if( checkConnected(board[c][r], board[c+1][r], board[c+2][r], board[c+3][r]) ){ //In each row, check if 4 pieces are connected horizontally
							return board[c][r]; //Return the winning player
						}
					}
					
					if(r<3&&c<4){ //Prevent out of bound error
						if( checkConnected(board[c][r], board[c+1][r+1], board[c+2][r+2], board[c+3][r+3]) ){ //Check diagonally, down right
							return board[c][r]; //Return the winning player
						}
					}
								
					if(r>3&&c<4){ //Prevent out of bound error
						if( checkConnected(board[c][r], board[c+1][r-1], board[c+2][r-2], board[c+3][r-3]) ){ //Check diagonally, down left
							return board[c][r]; //Return the winning player
						}
					}
					
				}			
			}
		}

	    return 0; //No player has won
	}
	
	//Method for looping through the board and setting all values to 0
	public static void resetBoard(){
		for(int br = 0; br<6; br++){
			for(int bc = 0; bc<7; bc++){
				board[bc][br] = 0;
			}
		}
		for(int r=0; r<6; r++){
			for(int c=0; c<7; c++){
				circles[c][r].setIcon(new ImageIcon(Game.class.getResource("circle.png")));
			}
		}
		playersTurn = 1;
	}
	
	//Method for checking if the board is filled up and there are no winners
	public static boolean isTie(){
		boolean boardFull = true;
		for(int br = 0; br<6; br++){
			for(int bc = 0; bc<7; bc++){
				if(board[bc][br]==0){
					boardFull = false;
					break;
				}
			}
		}
		
		if(boardFull && hasWon()==0){
			return true;
		}else{
			return false;
		}
	}
	
	//Method for printing the board in console for debugging purposes
	public static void printBoard(){
		for(int br = 0; br<6; br++){
			for(int bc = 0; bc<7; bc++){
				System.out.print(" " + board[bc][br]);
			}
			System.out.println("");
		}
		System.out.println("---------------");
	}

}
