package com.pubsub;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;
//This is the main class that is used to do publish,subscribe,create topic and view blog  
public class ClientPubSub {

    private String name="";		//name of user
	private JFrame frame;  
    
    JComboBox<String> listComboST,listComboScT,listComboSRT,listComboSRB,listComboScP,listComboSRTP,listComboSRBP,listComboSRP;
    JButton buttonNewTopic,buttonPublishNewBlog,buttonSubscribeNewBlog,buttonViewPublishedBlog,buttonReadBlog;
    JLabel ShowCartAllTimeLabel,ShowCartAllTime,ShowTotalPriceLabel,ShowTotalPrice,showtagname;
    JScrollPane SPScrollPane,scrollPaneVC,scrollPaneTA,scrollPaneST,scrollPaneRB;
    JTextField newTopicTextBox,creatorTextBox,writerNameTextBox,blogSubjectTextBox;
    JPanel paneltop,panelview,panelCreateTopic,panelSubscribeBlog;
    JTextArea writeContentTextBox;JTextPane viewContentTextBox;
    
    private Set<String> topicListCreatedByMe,AllPublisherListSubByMe,AllTopicListSubByMe;
    private HashMap<String,String> AllTopicListSub,AllTopicListPub;
    private HashMap<String,BlogMessage> receiveMessage;
    private Style defaultstyle,regularBlue,bold;
    private final static String LINK_ATTRIBUTE = "linkact";
    private StyledDocument doc;
   
    private MQConnector mqConnector;		//instance of MQConnector class used to communicate with MQ 
   
	public ClientPubSub(String name) {
		   this.name=name;
		   createGUI();
		   mqConnector = new MQConnector();
		   mqConnector.createConnection(); 	//connect with mq
		   
		   AllTopicListSubByMe = new HashSet<String>();				//list of topics subscribed
		   topicListCreatedByMe = new HashSet<String>();			//list of topics created
		   AllPublisherListSubByMe = new HashSet<String>();			//list of publishers subscribed
		   AllTopicListSub = new HashMap<String,String>();			//list of all topic available for subscription
		   AllTopicListPub = new HashMap<String,String>();			//list of all topic available for publish
		   receiveMessage = new HashMap<String,BlogMessage>();		//list of received messages
 	}
//	public static void main(String[] args) {
//		Client client = new Client("Dibyo");
//	}
   /* TK1 Ex3 */
   private void createGUI(){
	   frame = new JFrame(name);
	   frame.setSize(700,700);
	   frame.setLayout(null);
	   frame.setResizable(false);
	   frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
        	 mqConnector.closeConnection();		//close connection with mq
	         System.exit(0);
         }        
       });    

      addPanelTop();				//add all content of top panel
      addShowViewPanel();			//add all connent of view pannel
      frame.add(paneltop);
      frame.add(panelview);
      frame.setVisible(true);  
   }
   //pannel to show all form 
   private void addShowViewPanel(){
	   //panel for holding creatingtopic/writing blog & subscribe/view Blog panels
	   panelview = new JPanel();
	   panelview.setBackground(Color.RED);
	   CardLayout layout = new CardLayout();
	   layout.setHgap(10);
	   layout.setVgap(10);
	   panelview.setLayout(layout);        
	   panelview.setBounds(0, 60, 695, 600);
	   panelview.setVisible(true);

       //panel for creating topic and writing blog
       panelCreateTopic = new JPanel();
       panelCreateTopic.setBackground(Color.LIGHT_GRAY);
       panelCreateTopic.setLayout(null); 
       setpanelCreateTopic();			//menthod to show all item in createTopic panel 
       //panel for subscribing topic and viewing blog      
       panelSubscribeBlog = new JPanel();
       panelSubscribeBlog.setBackground(Color.LIGHT_GRAY);
       panelSubscribeBlog.setLayout(null);
       setpanelSubscribeBlog();			//menthod to show all item in subscribeBlog panel

       panelview.add("CreateTopic", panelCreateTopic);
       panelview.add("SubscribeBlog", panelSubscribeBlog);
   }
   //set components for laptop pannel
   private void setpanelSubscribeBlog(){
	   	//subscribe to new topic
	    JLabel headerNewTopicLabel = new JLabel("Subscribe to a New Topic : ");
	    headerNewTopicLabel.setBounds(0, 0, 200, 20);
	    headerNewTopicLabel.setForeground(Color.BLUE);
	    JLabel selectTopicLabel = new JLabel("Select Topic : ");
	    selectTopicLabel.setBounds(0, 30, 150, 20);
	    
	    JButton searchTopicSubButton = new JButton("Search Topics");	//button to get topic list
	    searchTopicSubButton.setBounds(165, 30, 150, 20);
	    searchTopicSubButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 searchTopicToSubscribe();		//call this method to search topic to subscribe
	    }   });
	    
	    listComboScT = new JComboBox<String>();							//list to show topics to subscribe
	    listComboScT.addItem("----Select Topic----");  
	    listComboScT.setBounds(330, 30, 150, 20);

	    JButton subscribeTopicButton = new JButton("Subscribe");		//button to subscribe to a topic
	    subscribeTopicButton.setBounds(575, 30, 100, 20);
	    subscribeTopicButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 subscribeToATopic();			//call this method to subscribe to a topic
	    }    });  
	    panelSubscribeBlog.add(headerNewTopicLabel);
	    panelSubscribeBlog.add(selectTopicLabel);
	    panelSubscribeBlog.add(searchTopicSubButton);
	    panelSubscribeBlog.add(listComboScT);
	    panelSubscribeBlog.add(subscribeTopicButton);
	    
	   	//subscribe to new publisher 
	    JLabel headerPublisherLabel = new JLabel("Subscribe to a New Publisher : ");
	    headerPublisherLabel.setBounds(0, 60, 200, 20);
	    headerPublisherLabel.setForeground(Color.BLUE);
	    JLabel selectPublisherLabel = new JLabel("Select Publisher : ");
	    selectPublisherLabel.setBounds(0, 90, 150, 20);
	    
	    JButton searchPublisherSubButton = new JButton("Search Publisher");		//button to search all publisher
	    searchPublisherSubButton.setBounds(165, 90, 150, 20);
	    searchPublisherSubButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 searchPublisherToSubscribe();			//call this method to search list of publishers
	    }	 });  

	    listComboScP = new JComboBox<String>();
	    listComboScP.addItem("----Select Publisher----");  
	    listComboScP.setBounds(330, 90, 150, 20);

	    JButton subscribePublisherButton = new JButton("Subscribe");			//button to subscribe to a publisher
	    subscribePublisherButton.setBounds(575, 90, 100, 20);
	    subscribePublisherButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 subscribeToAPublisher();				//call this method to subscribe to a publisher
	    }	 });  
	    panelSubscribeBlog.add(headerPublisherLabel);
	    panelSubscribeBlog.add(selectPublisherLabel);
	    panelSubscribeBlog.add(searchPublisherSubButton);
	    panelSubscribeBlog.add(listComboScP);
	    panelSubscribeBlog.add(subscribePublisherButton);
	    
	    //components for read blogs secion:
	    
	    //search by topic section:
	    JLabel headerReadBlogLabel = new JLabel("Read Subscribed Blog : ");
	    headerReadBlogLabel.setBounds(0, 130, 200, 20);
	    headerReadBlogLabel.setForeground(Color.BLUE);
	    
	    JButton searchByTopicSubViewButton = new JButton("Search by Topic");		//button to get list of subscribed topic
	    searchByTopicSubViewButton.setBounds(0, 150, 150, 20);
	    searchByTopicSubViewButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 searchSubscribedTopicToReadBlog();			//call this method to search list of topics subscribed by user  
	    }   });  

	    JLabel selectViewTopicLabel = new JLabel("Select Topic : ");
	    selectViewTopicLabel.setBounds(150, 170, 80, 20);
	    listComboSRT = new JComboBox<String>();										//list to show subscribed topics
	    listComboSRT.addItem("----Select Topic----");
	    listComboSRT.setBounds(230, 170, 150, 20);
	    listComboSRT.addItemListener(new ItemListener() { public void itemStateChanged(ItemEvent e) {
	        	 searchBlogUnderTopicToReadBlog();			//call this method to search list of blogs under topics received by user
	    }   });  

	    JLabel selectViewBlogLabel = new JLabel("Select Blog  : ");
	    selectViewBlogLabel.setBounds(380, 170, 80, 20);
	    listComboSRB = new JComboBox<String>();
	    listComboSRB.addItem("----Select Blog----");								//list to show list of blogs received for a topic  
	    listComboSRB.setBounds(455, 170, 150, 20);

	    JButton readBlogButton = new JButton("Read");								//button to read a specific blog
	    readBlogButton.setBounds(610, 170, 65, 20);
	    readBlogButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 readBlogSearchedByTopicBlog();				//call this methid to erad blog searched by topic->blog
	    }	});  

	    //Search by publisher section:
	    JButton searchByPubSubViewButton = new JButton("Search by Publisher");		//button to list subscribed publishers
	    searchByPubSubViewButton.setBounds(0, 210, 150, 20);
	    searchByPubSubViewButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 searchSubscribedPublisherToReadBlog();		//call this method to search subscribed publsher
	    }   });
	    
	    JLabel selectViewPublisherLabel = new JLabel("Select Publisher : ");
	    selectViewPublisherLabel.setBounds(150, 230, 110, 20);
	    listComboSRP = new JComboBox<String>();										//list to show list of subscribed publishers
	    listComboSRP.addItem("----Select Publisher----");
	    listComboSRP.setBounds(260, 230, 150, 20);
	    listComboSRP.addItemListener(new ItemListener() {  public void itemStateChanged(ItemEvent e) {
	        	 searchSubscribedTopicUnderPublisherToReadBlog();		//call this method to search topics under subscribed publsher
	    }   });  

	    JLabel selectPViewTopicLabel = new JLabel("Select Topic : ");
	    selectPViewTopicLabel.setBounds(430, 230, 110, 20);
	    listComboSRTP = new JComboBox<String>();						//list to show topics under subscribed publisher
	    listComboSRTP.addItem("----Select Topic----");
	    listComboSRTP.setBounds(525, 230, 150, 20);
	    listComboSRTP.addItemListener(new ItemListener() {  public void itemStateChanged(ItemEvent e) {
	        	 searchBlogUnderTopicUnderPublisherToReadBlog();		//call this method to search blog under topic under subscribed publsher
	    }   });  

	    JLabel selectPViewBlogLabel = new JLabel("Select Blog  : ");
	    selectPViewBlogLabel.setBounds(150, 270, 80, 20);
	    listComboSRBP = new JComboBox<String>();						//list to show blog under topic under publisher
	    listComboSRBP.addItem("----Select Blog----");  
	    listComboSRBP.setBounds(260, 270, 150, 20);

	    JButton readPBlogButton = new JButton("Read");
	    readPBlogButton.setBounds(610, 270, 65, 20);					//button to read blog by publisher->topic->blog
	    readPBlogButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	        	 readBlogSearchedByPublisherTopicBlog();				//call this methid to erad blog searched by publisher->topic->blog
	    }   });  
	    
	    viewContentTextBox = new JTextPane();									//Text area where content of blog will be displayed
	    viewContentTextBox.addMouseListener(new TextClickListener());			//for detecting clicks
	    viewContentTextBox.addMouseMotionListener(new TextMotionListener());	//for detecting motion
        doc = viewContentTextBox.getStyledDocument();							//get document reference of text box
        defaultstyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        regularBlue = doc.addStyle("regularBlue", defaultstyle);				//create the style for the hyperlink
        StyleConstants.setForeground(regularBlue, Color.BLUE);
        StyleConstants.setUnderline(regularBlue,true);
        regularBlue.addAttribute(LINK_ATTRIBUTE,new ShowLinkedBlog());
        bold = doc.addStyle("bold", defaultstyle);								//create the style for rest
        StyleConstants.setBold(bold, true);
        StyleConstants.setForeground(bold, Color.GRAY);
	    viewContentTextBox.setEditable(false);
	    viewContentTextBox.setBackground(Color.LIGHT_GRAY);
	    scrollPaneRB = new JScrollPane(viewContentTextBox);						//scroll panel that holds text box
	    scrollPaneRB.setBounds(20, 310, 630, 250);
	    scrollPaneRB.setPreferredSize(new Dimension(200, 180));
	    scrollPaneRB.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPaneRB.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    panelSubscribeBlog.add(headerReadBlogLabel);
	    panelSubscribeBlog.add(searchByTopicSubViewButton);
	    panelSubscribeBlog.add(selectViewTopicLabel);
	    panelSubscribeBlog.add(selectPViewTopicLabel);
	    panelSubscribeBlog.add(listComboSRP);
	    panelSubscribeBlog.add(listComboSRT);
	    panelSubscribeBlog.add(selectViewBlogLabel);
	    panelSubscribeBlog.add(listComboSRB);
	    panelSubscribeBlog.add(readBlogButton);
	    panelSubscribeBlog.add(searchByPubSubViewButton);
	    panelSubscribeBlog.add(selectViewPublisherLabel);
	    panelSubscribeBlog.add(listComboSRTP);
	    panelSubscribeBlog.add(selectPViewBlogLabel);
	    panelSubscribeBlog.add(listComboSRBP);
	    panelSubscribeBlog.add(readPBlogButton);
	    panelSubscribeBlog.add(scrollPaneRB);

   }
   //set components for smartphone pannel
   private void setpanelCreateTopic(){
	   	//create topic section:
	    JLabel headerNewTopicLabel = new JLabel("Create A New Blog Topic : ");
	    headerNewTopicLabel.setBounds(0, 0, 200, 20);
	    headerNewTopicLabel.setForeground(Color.BLUE);
	    
	    JLabel newTopicLabel = new JLabel("New Topic Name : ");
	    newTopicLabel.setBounds(20, 30, 110, 20);
	    newTopicTextBox = new JTextField(); 
	    newTopicTextBox.setBounds(130, 30, 150, 20);
	    
	    JLabel creatorLabel = new JLabel("Creator Name : ");
	    creatorLabel.setBounds(290, 30, 100, 20);
	    creatorTextBox = new JTextField(); 
	    creatorTextBox.setBounds(400, 30, 150, 20);
	    creatorTextBox.setText(this.name);
	    creatorTextBox.setEditable(false);
	    
	    JButton createTopicButton = new JButton("Create");				//button to create topic
	    createTopicButton.setBounds(595, 30, 80, 20);
	    createTopicButton.addActionListener(new ActionListener() {  public void actionPerformed(ActionEvent e) {
	    		createANewTopic();				//call this method to create topic
        }  });  
	    panelCreateTopic.add(headerNewTopicLabel);
	    panelCreateTopic.add(newTopicLabel);
	    panelCreateTopic.add(newTopicTextBox);
	    panelCreateTopic.add(creatorLabel);
	    panelCreateTopic.add(creatorTextBox);
	    panelCreateTopic.add(createTopicButton);
	    
	   	//publish blog section:
	    JLabel headerNewBlogLabel = new JLabel("Publish A New Blog  : ");
	    headerNewBlogLabel.setBounds(0, 80, 200, 20);
	    headerNewBlogLabel.setForeground(Color.BLUE);
	    
	    JButton searchTopicPubButton = new JButton("Search Topics");			//button to search topic to publish blog under the topic
	    searchTopicPubButton.setBounds(0, 110, 150, 20);
	    searchTopicPubButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	    		searchAllAvailableTopicToPublish();			//call this methd to seach list of all topics to publish bog under 
        }   });  
	    
	    JLabel selectTopicLabel = new JLabel("Select Topic : ");
	    selectTopicLabel.setBounds(0, 160, 150, 20);
	    listComboST = new JComboBox<String>();									//list to show all available topics
	    listComboST.addItem("----Select Topic----");
	    listComboST.setBounds(130, 160, 150, 20);

	    JLabel newWriterLabel = new JLabel("Writer Name : ");
	    newWriterLabel.setBounds(290, 160, 150, 20);

	    writerNameTextBox = new JTextField(); 									//text box to for writer name 
	    writerNameTextBox.setBounds(400, 160, 150, 20);
	    writerNameTextBox.setText(this.name);
	    writerNameTextBox.setEditable(false);
	    
	    JButton submitBlogButton = new JButton("Publish");						//button to publish blog
	    submitBlogButton.setBounds(595, 280, 80, 20);
	    submitBlogButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
	    		publishANewBlog();							//call this method to publish written blog
	    }   });  
	    
	    JLabel writeSubjectLabel = new JLabel("Blog Subject : ");
	    writeSubjectLabel.setBounds(0, 230, 100, 20);
	    blogSubjectTextBox = new JTextField(); 
	    blogSubjectTextBox.setBounds(130, 230, 420, 20);
	    JLabel writeContentLabel = new JLabel("Blog Content : ");
	    writeContentLabel.setBounds(0, 280, 100, 20);

	    writeContentTextBox = new JTextArea();						//text box to write blog 
	    scrollPaneST = new JScrollPane(writeContentTextBox);		//scroll pane that holds text box
	    scrollPaneST.setBounds(20, 310, 630, 250);
	    scrollPaneST.setPreferredSize(new Dimension(200, 180));
	    scrollPaneST.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPaneST.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    panelCreateTopic.add(headerNewBlogLabel);
	    panelCreateTopic.add(searchTopicPubButton);
	    panelCreateTopic.add(selectTopicLabel);
	    panelCreateTopic.add(listComboST);
	    panelCreateTopic.add(scrollPaneST);
	    panelCreateTopic.add(newWriterLabel);
	    panelCreateTopic.add(writerNameTextBox);
	    panelCreateTopic.add(writeSubjectLabel);
	    panelCreateTopic.add(writeContentLabel);
	    panelCreateTopic.add(blogSubjectTextBox);
	    panelCreateTopic.add(submitBlogButton);
   }
   //set top panel in frame to select pannels for viewing create topic/write blog and subscribe topic/read blog
   private void addPanelTop(){
	      paneltop = new JPanel(null);
	      paneltop.setBounds(0, 0, 700, 50);
	      buttonNewTopic = new JButton("Create Topic and Write Blog");	//button to show create topic/write blog pannel
	      buttonNewTopic.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
	        	 CardLayout cardLayout = (CardLayout)(panelview.getLayout());
	             cardLayout.show(panelview, "CreateTopic");
	      }  });
	      buttonSubscribeNewBlog = new JButton("Subscribe Topic and Read Blog");	//button to show subscribe topic/read blog pannel
	      buttonSubscribeNewBlog.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { 
	        	 CardLayout cardLayout = (CardLayout)(panelview.getLayout());
	             cardLayout.show(panelview, "SubscribeBlog");
	      }  });

	      JToolBar topToolBar = new JToolBar();		//tool bar to add above two buttons
	      topToolBar.add(buttonNewTopic);
	      topToolBar.add(buttonSubscribeNewBlog);
	      topToolBar.setBounds(0, 0, 750, 25);
	      topToolBar.setVisible(true);
	      topToolBar.addSeparator();
	      topToolBar.setFloatable(false);
	      paneltop.add(topToolBar);
	      paneltop.setVisible(true);
}
   //method to publish a new blog
   //this methods published a new blog, checks if topic selected, writer name given, subject mentioned and blog content not enpty
   private void publishANewBlog(){		//check of topic,writer name, blog subject and blog content not null
  	 	if(writeContentTextBox.getText().trim().equals("")){
  	 		JOptionPane.showMessageDialog(frame, "Topic content Empty");
  	 	}
  	 	else if(writerNameTextBox.getText().trim().equals("")){
  	 		JOptionPane.showMessageDialog(frame, "Writer name Empty");
  	 	}
  	 	else if(blogSubjectTextBox.getText().trim().equals("")){
  	 		JOptionPane.showMessageDialog(frame, "Subject Empty");
  	 	}
  	 	else if(((String)listComboST.getSelectedItem()).trim().equals("----Select Topic----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a topic");
  	 	}
  	 	else{
  	 		for(Map.Entry<String, String> entry : AllTopicListPub.entrySet()){				//get the actual topic name as publisher.topic
  	 			if(entry.getKey().equals(((String)listComboST.getSelectedItem()).trim())){
  	 				//call method to publish topic 
  	 				mqConnector.publishTopic(entry.getValue(),writeContentTextBox.getText().trim(),
  	 							 writerNameTextBox.getText().trim(),blogSubjectTextBox.getText().trim());
	    	   		JOptionPane.showMessageDialog(frame, "Blog posted Sucessfully");
	    	   		writeContentTextBox.setText("");
	    	   		blogSubjectTextBox.setText("");
  	 			}
  	 		}
  	 	}
   }
   //method to fetch all available topics to publish under
   //this methods searches all available topic so that user can publish blog under it
   private void searchAllAvailableTopicToPublish(){
	   AllTopicListPub = mqConnector.searchTopics();			//get list of all topics from ACTIVEMQ provider
	   listComboST.removeAllItems();
	   listComboST.addItem("----Select Topic----");
	   for(Map.Entry<String, String> entry : AllTopicListPub.entrySet()){
			 listComboST.addItem((String) entry.getKey());		//add all topics to list
	   }
   }
   //this methods creates a new  topic, checks if topic name field not empty and publisher name not empty   
   private void createANewTopic(){
  	 	boolean topicExistFlag = false;
  	 	if(newTopicTextBox.getText().trim().equals("")){
  	 		JOptionPane.showMessageDialog(frame, "Topic Name Empty");
  	 	}
  	 	else if(creatorTextBox.getText().trim().equals("")){
  			JOptionPane.showMessageDialog(frame, "Publisher Name Empty");
  	 	}
  	 	else{
	       	 HashMap<String,String> alreadyCreatedTopicList = new HashMap<String,String>(); 
	       	 alreadyCreatedTopicList = mqConnector.searchTopics();							//get all the topic listed in ACTIVEMQ provider
	       	 for(Map.Entry<String, String> entry : alreadyCreatedTopicList.entrySet()){		//check if topic with same name exists
      		 	if(entry.getKey().equals(newTopicTextBox.getText().trim())){
      		 		JOptionPane.showMessageDialog(frame, "This topic Exists, Choose another topic name");
      		 		newTopicTextBox.setText("");
      		 		topicExistFlag = true;
      		 		break;
      		 	}
			 }
	       	 if(topicExistFlag==false){											//if topic with same name does not exist create topic
	       		mqConnector.createTopic(creatorTextBox.getText().trim(),newTopicTextBox.getText().trim());	//call methid to create topic
				 topicListCreatedByMe.add(newTopicTextBox.getText().trim());
	       		 JOptionPane.showMessageDialog(frame, "Topic created Sucessfully");
	       		 newTopicTextBox.setText("");
	       	 }
  		 }
    }
   // read recived messages serached by Publisher->topic-> blog
   //this methods reads the blog details from received message list, where blog is selected by (publisher,topic,blog) pair, Checks if (publisher,tpic,blog) selected
   private void readBlogSearchedByPublisherTopicBlog(){		//check if publisher,topic,blog selected
  	 	if(((String)listComboSRP.getSelectedItem()).trim().equals("----Select Publisher----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a publisher to read");
  	 	}
  	 	else if(((String)listComboSRTP.getSelectedItem()).trim().equals("----Select Topic----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a blog to read");
  	 	}
  	 	else if(((String)listComboSRBP.getSelectedItem()).trim().equals("----Select Blog----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a blog to read");
  	 	}
  	 	else{		//fetch all received message and compare, if no match found no message published after subscription to the topic/publisher
  	 		receiveMessage=mqConnector.getReceivedMessage();
  	 		for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){				 
  	 			if(entry.getValue().getPublisher().equals((String)listComboSRP.getSelectedItem()) &&
				   entry.getValue().getTopic().equals((String)listComboSRTP.getSelectedItem()) &&
			       entry.getValue().getSubject().equals((String)listComboSRBP.getSelectedItem())){
  	 					showText(entry.getValue().getBody());		//call this method to show the message body
  	 			}
  	 		}
  	 	}
   }
   // fetch list of blogs searched by publihser->topic
   //this methods searches received blogs filered by publisher->topic and lists them to listcombo   
   private void searchBlogUnderTopicUnderPublisherToReadBlog(){
 	  	if(listComboSRTP.getSelectedIndex()>0){
 	  		listComboSRBP.removeAllItems();
 	  		listComboSRBP.addItem("----Select Blog----");
  	 		receiveMessage=mqConnector.getReceivedMessage();		//fetch all message from mqconnector
 	  		for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){
 	  			if(entry.getValue().getTopic().equals((String)listComboSRTP.getSelectedItem())){
 					 listComboSRBP.addItem(entry.getValue().getSubject());
 	  			}
 	  		}
 	  	}
   }
   // fetch list of topics under subscribed publihser
   //this methods searches subscribed topics filered by publisher and lists them to listcombo   
   private void searchSubscribedTopicUnderPublisherToReadBlog(){
 	  	if(listComboSRP.getSelectedIndex()>0){
 	  		listComboSRTP.removeAllItems();
 	  		listComboSRTP.addItem("----Select Topic----");
  	 		receiveMessage=mqConnector.getReceivedMessage();		//fetch all messages from mqconnector
 	  		for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){
 	  			if(entry.getValue().getPublisher().equals((String)listComboSRP.getSelectedItem())){
 					 listComboSRTP.addItem(entry.getValue().getTopic());
 	  			}
 	  		}
 	  	}
   }
   //fetch list of subscribed publihser
   //this methods searches subscribed publisher and lists them to listcombo   
   private void searchSubscribedPublisherToReadBlog(){
  	 	Iterator<String> itr = AllPublisherListSubByMe.iterator();
  	 	listComboSRP.removeAllItems();
  	 	listComboSRP.addItem("----Select Publisher----");
  	 	while(itr.hasNext()){
  	 		listComboSRP.addItem((String) itr.next());
  	 	}
   }
   //fetch list of blogs searched by publihser->topic
   //this methods reads blog from received message list where blog is selected by (topic,blog) pair only,check if (topic,blog) selected 
   private void readBlogSearchedByTopicBlog(){
  	 	if(((String)listComboSRT.getSelectedItem()).trim().equals("----Select Topic----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a topic to read");
  	 	}
  	 	else if(((String)listComboSRB.getSelectedItem()).trim().equals("----Select Blog----")){
  	 		JOptionPane.showMessageDialog(frame, "Select a blog to read");
  	 	}
  	 	else{	//fetch all received message and compare, if no match found no message published after subscription to the topic/publisher
  	 		receiveMessage=mqConnector.getReceivedMessage();
  	 		for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){
  	 			if(entry.getValue().getTopic().equals((String)listComboSRT.getSelectedItem()) &&
  	 			   entry.getValue().getSubject().equals((String)listComboSRB.getSelectedItem())){
  	 						showText(entry.getValue().getBody());						//call this method to show the message body
  	 			}
  	 		}
  	 	}
   }
   //fetch list of blogs searched by topic
   //this methods searches received blogs filered by topic and lists them to listcombo   
   private void searchBlogUnderTopicToReadBlog(){
 	  	if(listComboSRT.getSelectedIndex()>0){
 	  		listComboSRB.removeAllItems();
 	  		listComboSRB.addItem("----Select Blog----");
  	 		receiveMessage=mqConnector.getReceivedMessage();		//fetch list of messages from mqconnector
 	  		for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){
 	  			if(entry.getValue().getTopic().equals((String)listComboSRT.getSelectedItem())){
 					 listComboSRB.addItem(entry.getValue().getSubject());
 	  			}
 	  		}
 	  	}
   }
   //fetch list of subscribed topic
   //this methods searches list of subscribed topics and lists them to listcombo   
   private void searchSubscribedTopicToReadBlog(){
  	 	Iterator<String> itr = AllTopicListSubByMe.iterator();
  	 	listComboSRT.removeAllItems();
  	 	listComboSRT.addItem("----Select Topic----");
  	 	while(itr.hasNext()){
  	 		listComboSRT.addItem((String) itr.next());
  	 	}
   }
   //subscribed to a publisher
   //this methods subscribes to a pubilshed   
   private void subscribeToAPublisher(){
  	 	if(((String)listComboScP.getSelectedItem()).trim().equals("----Select Publisher----")){		//check if published selected
  	 		JOptionPane.showMessageDialog(frame, "Select a Publisher to subscribe");
  	 		return;
  	 	}
  	 	else{
  	 		mqConnector.subscribePublisher(((String)listComboScP.getSelectedItem()).trim());		//call this method to subscribe to the published
  	 		Set<String> AllTopicByPublisherList = new HashSet<String>();				//add published to subscribed publisher list
  	 		AllTopicByPublisherList = mqConnector.searchAllTopicOfPublisher(((String)listComboScP.getSelectedItem()).trim());
  	 		Iterator<String> itr = AllTopicByPublisherList.iterator();
  	 		while(itr.hasNext()){
  	 			AllTopicListSubByMe.add((String) itr.next());							//add topics under publisher to subscribed topics
  	 		}
  	 		AllPublisherListSubByMe.add(((String)listComboScP.getSelectedItem()).trim());
  	 		JOptionPane.showMessageDialog(frame, "Subscribed to Publisher Sucessfully");
  	 	}
   }
   //fetch list of subscribed publihser
   //this methods fetches list of pubilsher     
   private void searchPublisherToSubscribe(){
  	 	Set<String> AllPublisherList = new HashSet<String>();
  	 	AllPublisherList = mqConnector.searchPublisher();
  	 	Iterator<String> itr = AllPublisherList.iterator();
  	 	listComboScP.removeAllItems();
  	 	listComboScP.addItem("----Select Publisher----");
  	 	while(itr.hasNext()){
  	 		listComboScP.addItem((String) itr.next());
  	 	}
   }
   //fetch list of topics to subscribe
   //this methods fetches list of topics 
   private void searchTopicToSubscribe(){
  	 	AllTopicListSub = mqConnector.searchTopics();		//get list from mqconnetor
  	 	listComboScT.removeAllItems();
  	 	listComboScT.addItem("----Select Topic----");
		for(Map.Entry<String, String> entry : AllTopicListSub.entrySet()){
			listComboScT.addItem((String) entry.getKey());
		}
   }
   //subscribe to a topic
   //this methods subscribes to a specific topic
   private void subscribeToATopic(){
	   boolean subscribed = false;
  	   if(((String)listComboScT.getSelectedItem()).trim().equals("----Select Topic----")){
  		   JOptionPane.showMessageDialog(frame, "Select a Topic to subscribe");
  		   return;
  	   }
  	   for(Map.Entry<String, String> entry : AllTopicListSub.entrySet()){			//iterate through all topic list and subscribe to the actual one as woner.topic
  		   if(entry.getKey().equals(((String)listComboScT.getSelectedItem()).trim())){
  			   	 mqConnector.subscribeTopic(entry.getValue());				//call this method to subscribe to ACTIVEMQ
	        	 subscribed = true;
	        	 AllTopicListSubByMe.add(((String)listComboScT.getSelectedItem()).trim());	//add topic to subscribed topiclist
	        	 JOptionPane.showMessageDialog(frame, "Subscribed to Topic Sucessfully");
  		   }
  	   }
  	   if(subscribed == false){
  		   JOptionPane.showMessageDialog(frame, "Topic not found");
  	   }
   }
   //this methods makes call to active mq to subscribes to a specific topic
	//methid to display message body in viewContentTextBox
    private void showText(String content){
	   viewContentTextBox.setText("");
       String[] contentArray=content.split(" ");
	   try {       
		   for(int i=0;i<contentArray.length;i++){
			   if(contentArray[i].charAt(0)=='#'){	   doc.insertString(doc.getLength(),contentArray[i],regularBlue);	   }
			   else								 {	   doc.insertString(doc.getLength(),contentArray[i],bold);			   }
			   doc.insertString(doc.getLength()," ",bold);
		   }
		   viewContentTextBox.setCaretPosition(0);
		} catch (BadLocationException e) {			e.printStackTrace();		}
   }
   //nested class to make mouse click event on hyperlisked text 
   private class TextClickListener extends MouseAdapter {
       public void mouseClicked( MouseEvent e ) {
    	   try{
    		   Element elem = doc.getCharacterElement(viewContentTextBox.viewToModel(e.getPoint()));
    		   AttributeSet as = elem.getAttributes();
    		   ShowLinkedBlog newwindow = (ShowLinkedBlog)as.getAttribute(LINK_ATTRIBUTE);
    		   if(newwindow != null)
    			   newwindow.execute(doc.getText(elem.getStartOffset(),elem.getEndOffset()-elem.getStartOffset()));
    	   }catch(Exception x) {     x.printStackTrace();     }
        }
   }
   //nested class to make mouse pointer change event on hyperlinked text
   private class TextMotionListener extends MouseInputAdapter {
	   public void mouseMoved(MouseEvent e) {
		   Element elem = doc.getCharacterElement( viewContentTextBox.viewToModel(e.getPoint()));
		   AttributeSet as = elem.getAttributes();
		   if(StyleConstants.isUnderline(as))
			   viewContentTextBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		   else
			   viewContentTextBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	   }
   }
   //nested class to show related blogs on hyperlinked text
   private class ShowLinkedBlog extends AbstractAction{
	   
	   private Map<String,BlogMessage> relatedBlog;		//list to keep related message 
	   
	   ShowLinkedBlog(){
		   relatedBlog = new HashMap<String, BlogMessage>();
	   }
	   protected void execute(String topicname){
		    boolean topicSubscribed = false;
		   	topicname=topicname.substring(1);
      	 	Iterator<String> itr = AllTopicListSubByMe.iterator();
      	 	while(itr.hasNext()){									//check if clicked topic is sunscribed by user
      	 		if(topicname.equals((String) itr.next())){
      	 			topicSubscribed = true;
      	 			break;
      	 		}
      	 	}
		   if(!topicSubscribed){									//if not throgh message and dont show related message list on clicked topic
			   JOptionPane.showMessageDialog(frame, "You are not subscribed in the topic");
		   }
		   else{
			    relatedBlog.clear();								//if yes fetch all related blog from all received mesage list and regroup
	  	 		receiveMessage=mqConnector.getReceivedMessage();
			    for(Map.Entry<String, BlogMessage> entry : receiveMessage.entrySet()){
  		 			if(entry.getValue().getTopic().equals(topicname)){
  		 				relatedBlog.put(entry.getKey(),entry.getValue());
  		 			}
  		 		}
  			    if(relatedBlog.isEmpty()) {							//if no blog published under clicked topic 
  				   JOptionPane.showMessageDialog(frame, "No blog published yet under this topic");
  			    }
  			    else{
  			    	new PopUpFrame(relatedBlog);					//if there are published blogs call PopUPFrame and dispatch realted messgae on the clicked topic
  			    }
  		   }
	   }
	   public void actionPerformed(ActionEvent e)	{               /*execute();*/	   }
   }//end of ShowLinkedBlog class

}//end of Client class
