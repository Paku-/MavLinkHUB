package com.paku.mavlinkhub.mavlink;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;

public class MavLinkMsgItem implements Serializable{
	

	/**
	 * serializable MavlinkMsg extended with packet data and in the stream repetition count 
	 */
	private static final long serialVersionUID = -2616788128278070587L;
	
	MAVLinkMessage msg;
	int seqNo,sysId;
	
    private int count;	
	
    private String description_3;
    private String description_4;

    
    public MavLinkMsgItem(MAVLinkMessage msg,MAVLinkPacket pkt, int count) {
        super();
        this.count = count;
        this.msg = msg;
        this.seqNo = pkt.seq;
        this.sysId = pkt.sysid;
        
    }

    public MavLinkMsgItem(MAVLinkPacket pkt, int count) {
        super();
        this.count = count;
        this.msg = pkt.unpack();
        this.seqNo = pkt.seq;
        this.sysId = pkt.sysid;        
    }

       
    public String getCount(){
    	return String.valueOf(count);
    }
    
    public String getMainText(){
    	return this.toString();
    }
    
    public String getDescription_1(){
    	return "Desc1";
    }

    public String getDescription_2(){
    	return "Desc2";
    }

    public String getDescription_3(){
    	return description_3;
    }

    public String getDescription_4(){
    	return description_4;
    }

    @Override
    public String toString(){    	
    	return "SysId:"+sysId+" SeqNo:"+seqNo+" "+msg.toString();    	
    }
    
    
}
