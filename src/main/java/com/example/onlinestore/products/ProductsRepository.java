package com.example.onlinestore.products;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Mono;

public interface ProductsRepository extends R2dbcRepository<Product, Integer> {

	@Query("""
			INSERT INTO product(name, cost)
			VALUES(:name, :cost)
			""")
	Mono<Long> save(String name, BigDecimal cost);

	@Query("""
			SELECT id, name, cost
			FROM product
			ORDER BY RAND()
			LIMIT 1
			""")
	Mono<Product> findRandom();

}
