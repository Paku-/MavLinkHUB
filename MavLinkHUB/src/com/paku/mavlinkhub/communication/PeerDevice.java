package com.paku.mavlinkhub.communication;

public class PeerDevice {	
	
	String name;
	String address;
	
	
	public PeerDevice (String name,String address){
		this.name = name;
		this.address = address;
	}


	public String getName() {	
		return name;
	}

	
	public String getAddress() {	
		return address;
	}
	

}
