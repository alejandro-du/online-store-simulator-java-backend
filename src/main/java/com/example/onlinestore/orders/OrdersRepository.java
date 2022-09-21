package com.example.onlinestore.orders;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Mono;

public interface OrdersRepository extends R2dbcRepository<Order, Long> {

	@Query("""
			INSERT INTO order_item(order_id, product_id)
				SELECT :orderId, id
				FROM product
				ORDER BY RAND()
				LIMIT :count
			""")
	Mono<Long> saveRandomItems(Long orderId, int count);

}
