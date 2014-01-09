package com.paku.mavlinkhub.objects;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;

public class MavLinkMsgItem implements Serializable {

	/**
	 * serializable MavlinkMsg extended with packet data and the in-stream
	 * repetition count
	 */
	private static final long serialVersionUID = -2616788128278070587L;

	private MAVLinkMessage msg;
	private int seqNo;

	private int sysId;

	private int count; // how many times the the same msg was repeated

	public MavLinkMsgItem(MAVLinkMessage msg, MAVLinkPacket pkt, int count) {
		super();
		this.count = count;
		this.setMsg(msg);
		this.setSeqNo(pkt.seq);
		this.setSysId(pkt.sysid);

	}

	public MavLinkMsgItem(MAVLinkPacket pkt, int count) {
		super();
		this.count = count;
		this.setMsg(pkt.unpack());
		this.setSeqNo(pkt.seq);
		this.setSysId(pkt.sysid);
	}

	public String getCount() {
		return String.valueOf(count);
	}

	@Override
	public String toString() {
		return "SysId:" + getSysId() + " SeqNo:" + getSeqNo() + " " + getMsg().toString();
	}

	public MAVLinkMessage getMsg() {
		return msg;
	}

	public void setMsg(MAVLinkMessage msg) {
		this.msg = msg;
	}

	public int getSysId() {
		return sysId;
	}

	public void setSysId(int sysId) {
		this.sysId = sysId;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

}
