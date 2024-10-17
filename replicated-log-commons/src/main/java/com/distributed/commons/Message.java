package com.distributed.commons;

public class Message {
	private String message;
	private int w;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	@Override
	public String toString() {
		return "Message{" +
				"message='" + message + '\'' +
				", writeConcern=" + w +
				'}';
	}
}

