package com.paku.mavlinkhub.utils;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class HUBStats {

	private MAVLinkStats parserStatsDrone;
	private MAVLinkStats parserStatsGS;

	private int statsClientByteCount = 0;
	private int statsServerByteCount = 0;

	public HUBStats() {

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
		final String byteStats = "Client: " + statsClientByteCount + "[B] Server: " + statsServerByteCount + "[B]";

		if (((direction == MSG_SOURCE.FROM_DRONE) || (direction == MSG_SOURCE.FROM_ALL)) && (parserStatsDrone != null)) {
			return byteStats + "    MavLink Parser Stats [Pkg] - " + parserStatsDrone.receivedPacketCount
					+ " [CRC errors] - " + parserStatsDrone.crcErrorCount;
		}
		else if (((direction == MSG_SOURCE.FROM_GS) || (direction == MSG_SOURCE.FROM_ALL)) && (parserStatsGS != null)) {
			return byteStats + "    MavLink Parser Stats [Pkg] - " + parserStatsGS.receivedPacketCount
					+ " [CRC errors] - " + parserStatsGS.crcErrorCount;
		}
		else {
			return byteStats;
		}

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
