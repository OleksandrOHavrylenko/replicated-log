package com.distributed.master;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	private Long id;
	
	private String message;

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", message='" + message + '\'' +
				'}';
	}
}

