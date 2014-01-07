package com.paku.mavlinkhub.fragments;

public class MavlinkMsgItem {
	
	private String title;
    private String description;
 
    public MavlinkMsgItem(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }
    // getters and setters...
    
    public String getTitle(){
    	return title;
    }
    
    public String getDescription(){
    	return description;
    }

}
