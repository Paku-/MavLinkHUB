package com.paku.mavlinkhub.fragments.viewadapter;

import java.io.Serializable;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;

public class ItemMavLinkMsg implements Serializable {

	/**
	 * serializable MavlinkMsg extended with packet data and the in-stream
	 * repetition count
	 */
	private static final long serialVersionUID = -2616788128278070587L;

	private MAVLinkMessage msg;
	private int count; // how many times the the same msg was repeated
	private int seqNo;

	public ItemMavLinkMsg(MAVLinkPacket pkt, int count) {
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

}
