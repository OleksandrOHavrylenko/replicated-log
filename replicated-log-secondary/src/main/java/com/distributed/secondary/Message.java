package com.distributed.secondary;

import com.distributed.stubs.LogMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	private Long id;
	
	private String message;

	public Message(final LogMessage logMessage) {
		this.id = logMessage.getId();
		this.message = logMessage.getMessage();
	}

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", message='" + message + '\'' +
				'}';
	}
}

