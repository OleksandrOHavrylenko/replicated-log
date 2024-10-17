package com.distributed.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

	private String message;

	@JsonProperty("w")
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

