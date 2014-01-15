package com.paku.mavlinkhub.queue.msgcenter;

import android.content.Context;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.hubapp.HUBGlobals;

public class MavLinkMsgCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkMsgCollector";

	private final HUBGlobals globalVars;

	private MAVLinkStats parserStatsDrone, parserStatsGS;

	private ThreadMavLinkMsgParser parserThread;

	public MavLinkMsgCollector(Context mContext) {

		globalVars = ((HUBGlobals) mContext.getApplicationContext());

	}

	public void startMavLinkParserThread() {

		parserThread = new ThreadMavLinkMsgParser(globalVars);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		if (parserThread != null) parserThread.stopRunning();
	}

	public String getLastParserStats(MSG_SOURCE direction) {
		final String byteStats = "Transfer [Bytes] - " + globalVars.logger.statsReadByteCount;

		if ((direction == MSG_SOURCE.FROM_DRONE) && (parserStatsDrone != null)) {
			return byteStats + "    MavLink Parser Stats [Pkg Count] - " + parserStatsDrone.receivedPacketCount
					+ " [CRC errors] - " + parserStatsDrone.crcErrorCount;
		}
		else if ((direction == MSG_SOURCE.FROM_GS) && (parserStatsGS != null)) {
			return byteStats + "    MavLink Parser Stats [Pkg Count] - " + parserStatsGS.receivedPacketCount
					+ " [CRC errors] - " + parserStatsGS.crcErrorCount;
		}
		else {
			return byteStats;
		}

	}

	public void storeLastParserStats(MSG_SOURCE direction, MAVLinkStats stats) {
		if (direction == MSG_SOURCE.FROM_DRONE) parserStatsDrone = stats;
		if (direction == MSG_SOURCE.FROM_GS) parserStatsGS = stats;
	}

}
