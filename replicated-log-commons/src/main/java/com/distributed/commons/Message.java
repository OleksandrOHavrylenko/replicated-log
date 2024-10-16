package com.distributed.commons;

public class Message {
	private String message;
	private int writeConcern;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getWriteConcern() {
		return writeConcern;
	}

	public void setWriteConcern(int writeConcern) {
		this.writeConcern = writeConcern;
	}

	@Override
	public String toString() {
		return "Message{" +
				"message='" + message + '\'' +
				", writeConcern=" + writeConcern +
				'}';
	}
}

