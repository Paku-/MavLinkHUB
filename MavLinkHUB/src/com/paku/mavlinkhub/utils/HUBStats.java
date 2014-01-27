// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.utils;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class HUBStats {

	private MAVLinkStats parserStatsDrone;
	private MAVLinkStats parserStatsGS;

	private int statsClientByteCount = 0;
	private int statsServerByteCount = 0;

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

	public String toString_(MSG_SOURCE direction) {

		final String byteStats = "Q:" + statsQueueItemsCnt;

		String droneStats = " DR:" + statsClientByteCount + "/0/0";

		if (null != parserStatsDrone) {
			droneStats = " DR:" + statsClientByteCount + "/" + parserStatsDrone.receivedPacketCount + "/" + parserStatsDrone.crcErrorCount;
		}

		String gsStats = " GS:" + statsServerByteCount + "/0/0";
		if (null != parserStatsGS) {
			gsStats = " GS:" + statsServerByteCount + "/" + parserStatsGS.receivedPacketCount + "/" + parserStatsGS.crcErrorCount;
		}

		return byteStats + droneStats + gsStats;
	}

	private void clientAddBytes(int len) {
		statsClientByteCount += len;
	}

	private void serverAddBytes(int len) {
		statsServerByteCount += len;
	}

	public void setDroneStats(MAVLinkStats stats) {
		parserStatsDrone = stats;
	}

	public void setGSStats(MAVLinkStats stats) {
		parserStatsGS = stats;
	}

}
