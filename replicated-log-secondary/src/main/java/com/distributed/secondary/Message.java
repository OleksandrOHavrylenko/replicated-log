package com.distributed.secondary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	private Long id;
	
	private String message;

	public Message(final Long id, final String message) {
		this.id = id;
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", message='" + message + '\'' +
				'}';
	}
}

