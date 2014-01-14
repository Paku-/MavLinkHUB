package com.paku.mavlinkhub.mavlink;

import android.content.Context;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.HUBGlobals;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private HUBGlobals globalVars;

	private MAVLinkStats mMavlinkParserStats;

	private ThreadMavLinkParser parserThread;

	public MavLinkCollector(Context mContext) {

		globalVars = ((HUBGlobals) mContext.getApplicationContext());

	}

	public void startMavLinkParserThread() {

		parserThread = new ThreadMavLinkParser(globalVars);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		if (parserThread != null) parserThread.stopRunning();
	}

	public String getLastParserStats() {
		String byteStats = "Transfer [Bytes] - " + globalVars.logger.statsReadByteCount;
		if (mMavlinkParserStats != null)
			return byteStats + "    MavLink Parser Stats [Pkg Count] - " + mMavlinkParserStats.receivedPacketCount
					+ " [CRC errors] - " + mMavlinkParserStats.crcErrorCount;
		else
			return byteStats;

	}

	public void storeLastParserStats(MAVLinkStats stats) {

		mMavlinkParserStats = stats;

	}

}
