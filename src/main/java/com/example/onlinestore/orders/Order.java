package com.example.onlinestore.orders;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("order_")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	private Long id;
	private LocalDateTime time;

}
