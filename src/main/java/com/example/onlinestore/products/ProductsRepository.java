package com.example.onlinestore.products;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import reactor.core.publisher.Mono;

@Mapper
public interface ProductsRepository {

	@Insert("""
			INSERT INTO product(name, cost)
			VALUES(#{name}, #{cost})
			""")
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	Mono<Long> save(Product product);

	@Select("""
			SELECT id, name, cost
			FROM product
			ORDER BY RAND()
			LIMIT 1
			""")
	Mono<Product> findRandom();

	@Delete("DELETE FROM product")
	Mono<Long> deleteAll();

	@Select("SELECT COUNT(id) FROM product")
	Mono<Long> count();

}
