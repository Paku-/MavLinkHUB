package com.paku.mavlinkhub.queue.items;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class ItemMavLinkMsg implements Serializable {

	/**
	 * serializable MavlinkMsg extended with packet data and the in-stream
	 * repetition count
	 */
	private static final long serialVersionUID = -2616788128278070587L;

	private final int count; // how many times the the same msg was repeated
	private int seqNo;
	private MAVLinkMessage msg;
	private long timestamp;
	private MSG_SOURCE direction;

	public ItemMavLinkMsg(MAVLinkPacket pkt, MSG_SOURCE direction, int count) {
		super();
		this.count = count;
		setMsg(pkt.unpack());
		setSeqNo(pkt.seq);
		setSysId(pkt.sysid);
		setTimestamp(System.currentTimeMillis());
		setDirection(direction);
	}

	public String getCount() {
		return String.valueOf(count);
	}

	@Override
	public String toString() {
		return "SysId:" + getSysId() + " SeqNo:" + getSeqNo() + " " + getMsg().toString();
	}

	public String humanDecode() {
		return toString();
	}

	public MAVLinkMessage getMsg() {
		return msg;
	}

	public void setMsg(MAVLinkMessage msg) {
		this.msg = msg;
	}

	public int getSysId() {
		return msg.sysid;
	}

	public void setSysId(int sysId) {
		this.msg.sysid = sysId;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public MSG_SOURCE getDirection() {
		return direction;
	}

	public void setDirection(MSG_SOURCE direction) {
		this.direction = direction;
	}

}
