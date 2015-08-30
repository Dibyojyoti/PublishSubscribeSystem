package com.pubsub;
//this class is used to represent a received message
public class BlogMessage {
	private String topic="";
	private String subject="";
	private String body="";
	private String publisher="";
	public BlogMessage(String topic,String subject,String body,String publisher) {
		this.topic=topic;
		this.subject=subject;
		this.body=body;
		this.publisher = publisher;
	}
	public String getTopic(){return this.topic;}
	public String getSubject(){return this.subject;}
	public String getBody(){return this.body;}
	public String getPublisher(){return this.publisher;}
}
