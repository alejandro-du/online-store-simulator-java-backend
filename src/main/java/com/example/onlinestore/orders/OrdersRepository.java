package com.example.onlinestore.orders;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import reactor.core.publisher.Mono;

@Mapper
public interface OrdersRepository {

	@Insert("INSERT INTO order_(time) VALUES(#{time})")
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	Mono<Long> save(Order order);

	@Insert("""
			INSERT INTO order_item(order_id, product_id)
				SELECT #{orderId}, id
				FROM product
				ORDER BY RAND()
				LIMIT #{count}
			""")
	Mono<Long> saveRandomItems(Long orderId, int count);

	@Delete("DELETE FROM order_")
	Mono<Long> deleteAll();

}
