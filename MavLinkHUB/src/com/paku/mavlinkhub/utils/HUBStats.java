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

		if (parserStatsDrone != null) {
			droneStats = " DR:" + statsClientByteCount + "/" + parserStatsDrone.receivedPacketCount + "/" + parserStatsDrone.crcErrorCount;
		}

		String gsStats = " GS:" + statsServerByteCount + "/0/0";
		if (parserStatsGS != null) {
			gsStats = " GS:" + statsServerByteCount + "/" + parserStatsGS.receivedPacketCount + "/" + parserStatsGS.crcErrorCount;
		}

		return byteStats + droneStats + gsStats;
		/*
		 * if (((direction == MSG_SOURCE.FROM_DRONE) || (direction ==
		 * MSG_SOURCE.FROM_ALL)) && (parserStatsDrone != null)) { return
		 * byteStats + "    MavLink Parsers [Pkg]:" +
		 * parserStatsDrone.receivedPacketCount + " [Q]:" + statsQueueItemsCnt +
		 * " [CRCe]:" + parserStatsDrone.crcErrorCount; } else if (((direction
		 * == MSG_SOURCE.FROM_GS) || (direction == MSG_SOURCE.FROM_ALL)) &&
		 * (parserStatsGS != null)) { return byteStats +
		 * "    MavLink Parsers[Pkg]: " + parserStatsGS.receivedPacketCount +
		 * " [Q]:" + statsQueueItemsCnt + " [CRCe]:" +
		 * parserStatsGS.crcErrorCount; } else { return byteStats; }
		 */
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
