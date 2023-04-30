package com.example.onlinestore.products;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Mono;

public interface ProductsRepository extends R2dbcRepository<Product, Integer> {

	@Query("""
			SELECT id, name, cost
			FROM products
			ORDER BY RAND()
			LIMIT 1
			""")
	Mono<Product> findRandom();

}
