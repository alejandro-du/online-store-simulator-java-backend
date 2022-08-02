package com.example.orders;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Order {

	private long id;
	private LocalDateTime time;
	
}
