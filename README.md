## PublishSubscribeSystem
Publish/Subscribe application, in Distributed Systems Project in Winter 2014-15 semester at Technische Universität Darmstadt.

Group Members

1. Dibyojyoti Sanyal (https://github.com/Dibyojyoti) (@Dibyojyoti)
2. Anirban Chatterjee (https://github.com/anirban99) (@anirban99)
3. Pranay Sarkar (https://github.com/pranay22) (@pranay22)

This is Micro Blog which shows a publish/subscribe application built using Java Active MQ  and JMS technology.

This is an implementation of a Micro Blog System which handles the communication using the publish/subscribe paradigm. To implement the solution you have to use the Java Message Service (JMS). Any user of the system acts as producer and consumer at the same time.

*Functional Requirements:*

1. The GUI asks the user name to login on startup.
2. Every user can publish messages and can add tags for every message.
3. Every user can subscribe other users or tags.
4. JMS coordination: There is no server anymore. Therefore, each client manages his own blog and exchanges the related information with the other clients.

*Non Functional Requirements:*

1. Use the Model-View-Control pattern
2. Use Apache ActiveMQ Version 5.6.0 as message broker.
(http://activemq.apache.org/download.html).
You can install it in the VM by executing the command “sudo apt-get install activemq” in linux.
3. The ActiveMQ need to be started before manually to execute the application.

*How to execute*

Steps to run the application

1. Start Active MQ
2. Run the Build.xml it will start two login page.
3. Input user name to start the clients. Two client should not have the same name.


*How it works:*

1. any user can create as many topic they want but to the user the topic names are presented as they name it But actually the topics are created as <username>.<topicname>
2. By implementing this a user can subscribe to a perticular publisher, if any one subscribe to a publisher he is automatically subscribed to all the topics created by that publisher.
3. To subscribe to a topic user can search for topics and then subscribe to a single topic. 
4. To subscribe to a publisher user can search for publisher and then subscribe to the publisher. the user name given in login screen will be taken as publiser name and displayed.
5. To publish a blog under a topic search for all available topic and select any one and write the subject and content of the topic and publish.
6. User can publish blog under any topic. 
7. To read blog user can search by topic or by publisher. user can search by publisher if he is subscribed to a publisher. But can search by topic if he is subscribed to publisher or topic.
8.The blog subject will be populated in 'Select Blog' list. user can select any one of them and Read the blog.

*	The # tag works as following:*
9. when writing content of a topic user can specify any already existing topic using has tag ( like #topicname). and publish. while subscriber read the blog the hashtag will be click enabled. if user us subscribed to the topic and subscriber received blogs for the topic user can read all realted blogs by clicking the #tag otherwise specific mesages will be shown.

Note:
1. If you want to run the Build.xml again, restart the ActiveMQ because the topics that are created will be listed in ActiveMQ but as the subscribers are not made durable, they will be deleted once the client portal is closed.  
