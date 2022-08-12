package com.example.onlinestore;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductsRepository {

	@Insert("""
			INSERT INTO product(name, description, cost)
			VALUES(#{name}, #{description}, #{cost})
			""")
	void save(String name, String description, BigDecimal cost);

	@Select("""
			SELECT id, name, cost
			FROM product
			ORDER BY RAND()
			LIMIT 1
			""")
	Product findRandom();

	@Delete("DELETE FROM product")
	void deleteAll();

}
