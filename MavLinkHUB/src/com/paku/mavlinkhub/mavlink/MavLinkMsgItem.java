package com.paku.mavlinkhub.mavlink;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;

public class MavLinkMsgItem implements Serializable{
	

	/**
	 * serializable MavlinkMsg extended with packet data and the in-stream repetition count 
	 */
	private static final long serialVersionUID = -2616788128278070587L;
	
	MAVLinkMessage msg;
	int seqNo,sysId;
	
    private int count; // how many times the the same msg was repeated 	
	   
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
    
    @Override
    public String toString(){    	
    	return "SysId:"+sysId+" SeqNo:"+seqNo+" "+msg.toString();    	
    }
    
    
}
