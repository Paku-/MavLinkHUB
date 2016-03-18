// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.utils;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class HUBStats {

	private MAVLinkStats parserStatsDrone;
	private MAVLinkStats parserStatsGS;

	private int statsDroneByteCount = 0;
	private int statsGSByteCount = 0;
	/*
		private float lastDroneMsgTime = 0;
		private float lastGSMsgTime = 0;

		private float avrDroneMsgTime = 0;
		private float avrGSMsgTime = 0;

		private ArrayDeque<MAVLinkMessage> statsDroneMsgs;
	*/
	private int statsQueueItemsCnt = 0;

	public HUBStats() {

	}

	public void setQueueItemsCnt(int cnt) {
		statsQueueItemsCnt = cnt;
	}

	public int getQueueItemsCnt() {
		return statsQueueItemsCnt;
	}

	public void setParserStats(MSG_SOURCE direction, MAVLinkStats stats) {
		if (direction == MSG_SOURCE.FROM_DRONE) setDroneStats(stats);
		if (direction == MSG_SOURCE.FROM_GS) setGSStats(stats);
	}

	public void addByteStats(MSG_SOURCE direction, int len) {
		if (direction == MSG_SOURCE.FROM_DRONE) clientAddBytes(len);
		if (direction == MSG_SOURCE.FROM_GS) serverAddBytes(len);
	}

	public String toString(MSG_SOURCE direction) {

		final String byteStats = "Q:" + statsQueueItemsCnt;

		String droneStats = " DR:" + statsDroneByteCount + "/0/0";

		if (null != parserStatsDrone) {
			droneStats = " DR:" + statsDroneByteCount + "/" + parserStatsDrone.receivedPacketCount + "/" + parserStatsDrone.crcErrorCount;
		}

		String gsStats = " GS:" + statsGSByteCount + "/0/0";
		if (null != parserStatsGS) {
			gsStats = " GS:" + statsGSByteCount + "/" + parserStatsGS.receivedPacketCount + "/" + parserStatsGS.crcErrorCount;
		}

		return byteStats + droneStats + gsStats;
	}

	private void clientAddBytes(int len) {
		statsDroneByteCount += len;
	}

	private void serverAddBytes(int len) {
		statsGSByteCount += len;
	}

	public void setDroneStats(MAVLinkStats stats) {
		parserStatsDrone = stats;
	}

	public void setGSStats(MAVLinkStats stats) {
		parserStatsGS = stats;
	}

}
