package com.paku.mavlinkhub.mavlink;

import android.content.Context;

import com.MAVLink.Messages.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.objects.ItemMavLinkMsg;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	private MAVLinkStats mMavlinkParserStats;

	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mContext) {

		globalVars = ((AppGlobals) mContext.getApplicationContext());

	}

	public void startMavLinkParserThread() {

		parserThread = new MavLinkParserThread(globalVars);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		if (parserThread != null) parserThread.stopRunning();
	}

	public String decodeMavlinkMsgItem(ItemMavLinkMsg lastMavLinkMsgItem) {
		return lastMavLinkMsgItem.toString();
	}

	public String decodeMavlinkPkg(MAVLinkPacket pkg) {

		return "sysId: " + pkg.sysid + " seqNo: " + pkg.seq;

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
