package com.example.onlinestore.products;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	private Integer id;
	private String name;
	private BigDecimal cost;

}
