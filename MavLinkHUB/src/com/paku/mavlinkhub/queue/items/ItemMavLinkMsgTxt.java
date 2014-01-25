package com.paku.mavlinkhub.queue.items;

import com.MAVLink.Messages.ApmModes;
import com.MAVLink.Messages.ardupilotmega.msg_heartbeat;
import com.MAVLink.Messages.ardupilotmega.msg_statustext;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.mavlink.MavLinkClassExtractor;

public class ItemMavLinkMsgTxt {

	public String msgName;
	public String mainTxt;
	public String desc_1;
	public String desc_2;
	public String desc_3;
	public String desc_4;
	public String desc_5;
	public MSG_SOURCE direction;

	public ItemMavLinkMsgTxt(ItemMavLinkMsg msgItem, MavLinkClassExtractor mavClasses) {
		//setMe(msgItem, mavClasses);
	}

	public void setMe(ItemMavLinkMsg msgItem, MavLinkClassExtractor mavClasses) {

		// MavLink package sender and sequence number
		direction = msgItem.direction;
		desc_1 = "[" + String.valueOf(msgItem.msg.sysid) + "]";
		desc_2 = "[" + String.valueOf(msgItem.seqNo) + "]";
		desc_4 = "[" + String.valueOf(msgItem.timestamp) + "]";

		switch (msgItem.msg.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			final msg_heartbeat msg_heartbeat_ = (msg_heartbeat) msgItem.msg;
			// setMsgName(msg_heartbeat_.toString().substring(0,msg_heartbeat_.toString().indexOf("-",
			// 0)-1));
			msgName = msg_heartbeat_.getClass().getSimpleName();

			// operation mode + state
			final ApmModes mode;
			mode = ApmModes.getMode(msg_heartbeat_.custom_mode, msg_heartbeat_.type);
			mainTxt = (mode.name() + " " + mavClasses.getMavState().get(msg_heartbeat_.system_status).getName());

			// ship's type name + autopilot name
			desc_3 = (mavClasses.getMavType().get(msg_heartbeat_.type).getName() + ":" + mavClasses.getMavAutopilot().get(msg_heartbeat_.autopilot).getName());

			break;

		case msg_statustext.MAVLINK_MSG_ID_STATUSTEXT:

			final msg_statustext msg_statustext_ = (msg_statustext) msgItem.msg;
			// setMsgName(msg_statustext_.toString().substring(0,msg_statustext_.toString().indexOf("-",
			// 0)-1));
			msgName = (msg_statustext_.getClass().getSimpleName());

			mainTxt = (String.valueOf(msg_statustext_.getText()));
			desc_3 = ("Severity:" + String.valueOf(msg_statustext_.severity));
			break;

		default:
			msgName = "*";
			mainTxt = msgItem.msg.toString();
			break;
		}

	}

}
