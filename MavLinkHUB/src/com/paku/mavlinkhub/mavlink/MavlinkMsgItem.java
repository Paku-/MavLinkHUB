package com.paku.mavlinkhub.mavlink;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;

public class MavlinkMsgItem {
	

	private String mainText;
    private String description_1;
    private String description_2;
    private String description_3;
    private String description_4;
    private int count;
    
    public MavlinkMsgItem(int count,String title, String desc1,String desc2,String desc3,String desc4) {
        super();
        this.count = count;
        this.mainText = title;
        this.description_1 = desc1;
        this.description_1 = desc2;
        this.description_1 = desc3;
        this.description_1 = desc4;
    }
    
    public String getCount(){
    	return String.valueOf(count);
    }
    
    public String getMainText(){
    	return mainText;
    }
    
    public String getDescription_1(){
    	return description_1;
    }

    public String getDescription_2(){
    	return description_2;
    }

    public String getDescription_3(){
    	return description_3;
    }

    public String getDescription_4(){
    	return description_4;
    }

    
    
}
