package com.pubsub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQTopic;
//This class handles all the calls amd management of ActiveMQ
public class MQConnector implements MessageListener{

    private static final String FACTORY_URL="tcp://localhost:61616"; 
    private static ActiveMQConnectionFactory topicConnectionFactory;
    private static TopicConnection connection = null;
    private static TopicSession session;
    private static Topic topicDestination;
    private static TopicPublisher publisher;
    private static TopicSubscriber subscriber;

    private HashMap<String,BlogMessage> receivedMessage;
    
	public MQConnector() {
			receivedMessage = new HashMap<String,BlogMessage>();
	}

	protected void subscribeTopic(String topic){
		   try{
		       // Create the destination (Topic or Queue)
			   topicDestination = session.createTopic(topic);
			   // Create a MessageSubscriber from the Session to the Topic or Queue
			   subscriber = session.createSubscriber(topicDestination);
			   subscriber.setMessageListener(this);
		   }catch (JMSException e) {
				System.out.println("Exception while subscribing to topic");
				e.printStackTrace();
	       }
	}
	   //this methods makes call to active mq to subscribes to a specific publisher
	protected void subscribePublisher(String publisher){
		   try{
		       // Create the destination (Topic or Queue)		   
			   topicDestination = session.createTopic(publisher+".*");
			   // Create a MessageSubscriber from the Session to the Topic or Queue
			   subscriber = session.createSubscriber(topicDestination);
			   subscriber.setMessageListener(this);
		   }catch (JMSException e) {
				System.out.println("Exception while subscribing to topic");
				e.printStackTrace();
	       }
		   
	 }
	   //this methods makes connection to active mq 
	 protected void createConnection(){
		   try{   
			   // Create a ConnectionFactory
			   topicConnectionFactory  = new ActiveMQConnectionFactory(FACTORY_URL);
			   // Create a Connection
			   connection = topicConnectionFactory.createTopicConnection();
			   connection.start();
			   // Create a Session
			   session = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);			
		   }catch (JMSException e) {		   System.out.println("Exception while setting up connection");		   e.printStackTrace();	   }
	 }
	   //this methods publishes topic to active mq
	 protected void publishTopic(String topic,String message,String publisherName,String subject){
			TextMessage msg;
			try {
				topicDestination = session.createTopic(topic);
				// Create a MessagePublisher from the Session to the Topic or Queue
				publisher = session.createPublisher (topicDestination);
				msg = session.createTextMessage();
				msg.setText(message);
				msg.setJMSCorrelationID(subject);
				publisher.publish(msg);
			} catch (JMSException e) {			e.printStackTrace();		}
	 }
	   //this methods makes connection to active mq and creates a topic
	 protected void createTopic(String creatorName,String newTopicName){
			try {
				// Create the destination (Topic or Queue)
				topicDestination = session.createTopic(creatorName+"."+newTopicName);
				// Create a MessagePublisher from the Session to the Topic or Queue
				publisher = session.createPublisher (topicDestination);
			} catch (JMSException e) {
				System.out.println("Exception while creating topic");			e.printStackTrace();		}
	 }
	   //this methods makes connection to active mq and fetches list of topics
	 protected HashMap<String,String> searchTopics(){
		   	HashMap<String,String> currentTopicList = new HashMap<String,String>();
			try{
				ActiveMQConnection activeMQConnection = ActiveMQConnection.makeConnection(FACTORY_URL);
				activeMQConnection.start();
				DestinationSource destinationSource = activeMQConnection.getDestinationSource();	
				Set<ActiveMQTopic> allTopic=destinationSource.getTopics();
				Iterator<ActiveMQTopic> itr= allTopic.iterator();
				while(itr.hasNext()){
					String topic=itr.next().getTopicName();
					currentTopicList.put(topic.substring(topic.indexOf(".")+1), topic);
				}
			} catch (Exception e) {			System.out.println("Exception while searching topics");			e.printStackTrace();		}
			return currentTopicList;
	  }
	   //this methods makes connection to active mq and fetches list of publishers
	  protected Set<String> searchPublisher(){
		   	Set<String> currentPublisher = new HashSet<String>(); 
			try{
				ActiveMQConnection activeMQConnection = ActiveMQConnection.makeConnection(FACTORY_URL);
				activeMQConnection.start();
				DestinationSource destinationSource = activeMQConnection.getDestinationSource();	
				Set<ActiveMQTopic> allTopic=destinationSource.getTopics();
				Iterator<ActiveMQTopic> itr= allTopic.iterator();
				while(itr.hasNext()){
					String topic=itr.next().getTopicName();
					currentPublisher.add(topic.substring(0,topic.indexOf(".")));
				}
			} catch (Exception e) {			System.out.println("Exception while searching publishers");			e.printStackTrace();		}
			return currentPublisher;
	   }
	   //this methods makes connection to active mq and returns list topic under a specific publisher   
	   protected Set<String> searchAllTopicOfPublisher(String publisher){
		   	Set<String> topicByPublisher = new HashSet<String>(); 
			try{
				ActiveMQConnection activeMQConnection = ActiveMQConnection.makeConnection(FACTORY_URL);
				activeMQConnection.start();
				DestinationSource destinationSource = activeMQConnection.getDestinationSource();	
				Set<ActiveMQTopic> allTopic=destinationSource.getTopics();
				Iterator<ActiveMQTopic> itr= allTopic.iterator();
				while(itr.hasNext()){
					String topic=itr.next().getTopicName();
					if(topic.substring(0,topic.indexOf(".")).equals(publisher)){
						topicByPublisher.add(topic.substring(topic.indexOf(".")+1));
					}
				}
			} catch (Exception e) {			System.out.println("Exception while searching topics of publisher");			e.printStackTrace();		}
			return topicByPublisher;
	   }
	    //overridding method to implement message listener to receiv messages from activemq asynchronously
	   public void onMessage(Message message) {
			String blogtopicname="";
			String blogsubject="";
			String blogbody="";
			String blogpublisher="";
	 	    //System.out.println("Message received:");
			try {
				//parse message
				blogtopicname = ""+((TextMessage) message).getJMSDestination();
				blogpublisher = blogtopicname.substring(blogtopicname.indexOf("://")+3,blogtopicname.indexOf("."));
				blogtopicname = blogtopicname.substring(blogtopicname.indexOf(".")+1);
				blogsubject=""+((TextMessage) message).getJMSCorrelationID();
				blogbody=""+((TextMessage) message).getText();
				receivedMessage.put(message.getJMSMessageID(),new BlogMessage(blogtopicname,blogsubject,blogbody,blogpublisher));	//put in a received message list
				
			} catch (JMSException e) {			e.printStackTrace();		}
		}
	   //to get list of messages
	   protected HashMap<String,BlogMessage> getReceivedMessage(){
		   return receivedMessage;
	   }
	   //to close connection
	   protected void closeConnection(){
			try {		connection.close();			} 
			catch (JMSException e) {System.out.println("Exception while closing connection");e.printStackTrace();		}
	   }
}
