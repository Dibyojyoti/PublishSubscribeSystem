package com.pubsub;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//This class is used to show related blogs when clicked on hyperlink
public class PopUpFrame {
	private JFrame frame;
	private JPanel showpanel;
	private JTextArea viewContentTextBox;
	private JScrollPane scrollPaneRB;
	public PopUpFrame(Map<String,BlogMessage> relatedBlog) {
		frame = new JFrame("Related Messages");
		frame.setBounds(350, 250, 500, 300);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent windowEvent){
	    	  frame.dispose();
	    }  });    
	    viewContentTextBox = new JTextArea();
	    viewContentTextBox.setEditable(false);
	    viewContentTextBox.setText("");
	    viewContentTextBox.setBackground(Color.LIGHT_GRAY);
	    scrollPaneRB = new JScrollPane(viewContentTextBox);
	    scrollPaneRB.setBounds(0, 0, 490, 272);
	    scrollPaneRB.setPreferredSize(new Dimension(200, 180));
	    scrollPaneRB.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPaneRB.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    frame.add(scrollPaneRB);
	    frame.setVisible(true);  
 		for(Map.Entry<String, BlogMessage> entry : relatedBlog.entrySet()){					//fetch messages and show
 				viewContentTextBox.append("Subject: "+entry.getValue().getSubject()+"\n");
 				viewContentTextBox.append(entry.getValue().getBody()+"\n\n");
 		}
	}
}
