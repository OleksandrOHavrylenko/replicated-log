package com.distributed.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Message {

    @NotNull
	private String message;

//	default value is one due to min 1 write on master node
	@JsonProperty("w")
	@Min(1)
	private int writeConcern = 1;

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

