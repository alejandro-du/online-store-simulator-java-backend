package com.example.onlinestore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

	private Long id;
	private LocalDateTime time;
	private Collection<Product> items;

	public BigDecimal getTotal() {
		return items == null ? BigDecimal.ZERO
				: items.stream()
						.map(Product::getCost)
						.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
