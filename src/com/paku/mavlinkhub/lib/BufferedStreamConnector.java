package com.paku.mavlinkhub.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.paku.mavlinkhub.MavLinkStuff;
import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;
import android.util.Log;
// this is a super class for connectors using buffered stream to keep incoming data
// receiving Fragment class has to register for IBufferReady interface to be called on data arrival. 

public abstract class BufferedStreamConnector {

	public ByteArrayOutputStream mConnectorStream;
	public boolean lockConnStream = false;
	private int streamFlushSize = 16;

	protected abstract boolean openConnection(String address); // throws
																// UnknownHostException,IOException;

	protected abstract void closeConnection(); // throws
												// UnknownHostException,IOException;

	protected abstract boolean isConnected();

	protected abstract String getPeerName();

	protected abstract void startConnectorReceiver(BluetoothSocket socket);
	
	
	//interface
	private IBufferReady callerFragment = null;
	private IBufferReady callerMavLink = null;
	
	public void registerForIBufferReady(Fragment fragment) {
		callerFragment = (IBufferReady) fragment;
	}

	public void registerForIBufferReady(MavLinkStuff mMavLinkStuff) {
		callerMavLink = (IBufferReady) mMavLinkStuff;
	}
	//end

	public BufferedStreamConnector(int capacity) {

		mConnectorStream = new ByteArrayOutputStream(capacity);
		mConnectorStream.reset();

	}

	public void waitForStreamLock() {
		while (lockConnStream) {
			;
		}

		lockConnStream = true;
	}

	public void releaseStream() {
		lockConnStream = false;
	}

	public void processConnectorStream() {
		boolean gotRead = false;

		if (mConnectorStream.size() > streamFlushSize) {

			Log.d("BUFFER", "Size [" + String.valueOf(mConnectorStream.size()) + "]:");

			if (callerFragment != null) {
				callerFragment.onBufferReady();
				gotRead = true;

			}

			if (callerMavLink != null) {
				callerMavLink.onBufferReady();
				gotRead = true;
			}
			// reset stream if at least one caller got it ...
			if (gotRead) resetStream(true);
		}
	}

	private void resetStream(boolean withLock) {
		if (withLock)
			waitForStreamLock();
		mConnectorStream.reset();
		if (withLock)
			releaseStream();
	}

	public void copyConnectorStream(OutputStream targetStream,boolean doReset)
			throws IOException {
		mConnectorStream.writeTo(targetStream);
		if (doReset) resetStream(false);
	}
	
	public ByteArrayOutputStream getConnectorStream(){
		return mConnectorStream;
	}


}
