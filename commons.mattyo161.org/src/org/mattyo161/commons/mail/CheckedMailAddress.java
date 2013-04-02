package org.mattyo161.commons.mail;

import org.apache.commons.collections.map.CaseInsensitiveMap;

public class CheckedMailAddress {
	public static final int NOTCHECKED = 0;
	public static final int VALID = 1;
	public static final int INVALID = 2;
	public static final int ERROR = 3;
	
	private String address = "";
	private int status = 0;
	private String message = "";
	
	public CheckedMailAddress() {
		super();
	}
	
	public CheckedMailAddress(String address, int status, String message) {
		super();
		this.address = address;
		this.status = status;
		this.message = message;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("['").append(this.address).append("',");
		switch (this.status) {
		case VALID:
			buff.append("VALID,");
			break;
		case INVALID:
			buff.append("INVALID,");
			break;
		case ERROR:
			buff.append("ERROR,");
			break;
		default:
			buff.append("NOTCHECKED,");
			break;
		}
		buff.append("'").append(this.message).append("']");
		return buff.toString();
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
