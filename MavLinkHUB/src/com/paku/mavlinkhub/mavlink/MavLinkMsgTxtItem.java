package com.paku.mavlinkhub.mavlink;

import com.MAVLink.Messages.ApmModes;
import com.MAVLink.Messages.ardupilotmega.msg_heartbeat;
import com.MAVLink.Messages.ardupilotmega.msg_statustext;
import com.MAVLink.Messages.enums.MAV_AUTOPILOT;

public class MavLinkMsgTxtItem {
	
	private String msgName;
	private String mainTxt;
	private String desc_1;
	private String desc_2;    
    private String desc_3;
    private String desc_4;
    private String desc_5;
    
    public MavLinkMsgTxtItem(MavLinkMsgItem msgItem){
    	setMe(msgItem);
    }
    
    
    private void setMe(MavLinkMsgItem msgItem){
    	
    	switch (msgItem.msg.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			msg_heartbeat msg_heartbeat_ = (msg_heartbeat) msgItem.msg;
			//setMsgName(msg_heartbeat_.toString().substring(0,msg_heartbeat_.toString().indexOf("-", 0)-1));
			setMsgName(msg_heartbeat_.getClass().getSimpleName());
			
			ApmModes mode;			
			
			mode = ApmModes.getMode(msg_heartbeat_.custom_mode, msg_heartbeat_.type);
			setMainTxt(mode.name());
			
			mode = ApmModes.getMode(msg_heartbeat_.base_mode, msg_heartbeat_.type);
			setDesc_3(MAV_AUTOPILOT.class.getDeclaredFields()[3].getName());
			
			break;
			
		case msg_statustext.MAVLINK_MSG_ID_STATUSTEXT:
			
			msg_statustext msg_statustext_ = (msg_statustext) msgItem.msg;
			//setMsgName(msg_statustext_.toString().substring(0,msg_statustext_.toString().indexOf("-", 0)-1));
			setMsgName(msg_statustext_.getClass().getSimpleName());
			
			setMainTxt(String.valueOf(msg_statustext_.getText()));
			setDesc_3("Severity:"+String.valueOf(msg_statustext_.severity));
			break;			

		default:
			break;
		}
    	
    	
		setDesc_1("["+String.valueOf(msgItem.sysId)+"]");
		setDesc_2("["+String.valueOf(msgItem.seqNo)+"]");
    	
    	
    	
    }
    
	public String getName() {
		return msgName;
	}


	public void setMsgName(String name) {
		this.msgName = name;
	}


	public String getMainTxt() {
		return mainTxt;
	}


	public void setMainTxt(String mainTxt) {
		this.mainTxt = mainTxt;
	}


	public String getDesc_1() {
		return desc_1;
	}


	public void setDesc_1(String desc_1) {
		this.desc_1 = desc_1;
	}


	public String getDesc_3() {
		return desc_3;
	}


	public void setDesc_3(String desc_3) {
		this.desc_3 = desc_3;
	}


	public String getDesc_2() {
		return desc_2;
	}


	public void setDesc_2(String desc_2) {
		this.desc_2 = desc_2;
	}


	public String getDesc_4() {
		return desc_4;
	}


	public void setDesc_4(String desc_4) {
		this.desc_4 = desc_4;
	}


	public String getDesc_5() {
		return desc_5;
	}


	public void setDesc_5(String desc_5) {
		this.desc_5 = desc_5;
	}
    

}
