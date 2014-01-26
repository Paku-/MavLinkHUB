package com.paku.mavlinkhub.enums;

public enum APP_STATE {

	// server
	MSG_SERVER_STARTED, MSG_SERVER_START_FAILED, MSG_SERVER_STOPPED, MSG_SERVER_CLIENT_CONNECTED, MSG_SERVER_CLIENT_DISCONNECTED,
	// client (drone)
	MSG_DRONE_CONNECTION_ATTEMPT_FAILED, MSG_DRONE_CONNECTED, MSG_DRONE_CONNECTION_LOST,
	// byte queues
	MSG_DATA_UPDATE_SYSLOG, MSG_DATA_UPDATE_BYTELOG, MSG_DATA_UPDATE_STATS,
	// BT Adapter
	REQUEST_ENABLE_BT,
	// Main Queue
	MSG_QUEUE_MSGITEM_SENT, MSG_QUEUE_MSGITEM_READY,
	// UI
	MSG_UI_MODE_CHANGED,

}
