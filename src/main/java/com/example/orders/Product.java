package com.example.orders;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Product {

	private int id;
	private String name;
	private BigDecimal cost;

}
