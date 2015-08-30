package com.pubsub;

//Login class
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class Login  {
    
   private JFrame frame;
   private JLabel header;
   private JPanel control;
    
   public Login(){
	   createGUI();
   }  
   /*Creates the GUI for login screen */
   private void createGUI(){
	   frame = new JFrame("Login to Connect");
	   frame.setSize(500,300);
	   frame.setLayout(new GridLayout(3, 1));
	   frame.setLocationRelativeTo(null);
	   frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
     header = new JLabel("", JLabel.CENTER);        
     control = new JPanel();
     control.setLayout(new FlowLayout());
	 frame.add(header);
     frame.add(control);   
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setVisible(true);  
   }
   /* Method for User Name input from the user */
   public void showUserName(){	 
	   header.setText("Please provide a Name to continue"); 	
	      
      JLabel  userName= new JLabel("Name: ", JLabel.CENTER);
      final JTextField userID = new JTextField(6);  
      JButton loginButton = new JButton("Enter");
      loginButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) { 
        	//Calls ClientPubSub.java
         	frame.dispose();
       	    new ClientPubSub(userID.getText()); 

         }
      }); 
      control.add(userName);
      control.add(userID);
      control.add(loginButton);
      frame.setVisible(true); 
   }
   public static void main(String[] args){
	      Login  loginID = new Login(); 
		  loginID.showUserName();	            
   } 
}
