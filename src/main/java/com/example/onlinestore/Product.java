package com.example.onlinestore;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Product {

	private Integer id;
	private String name;
	private BigDecimal cost;

}
