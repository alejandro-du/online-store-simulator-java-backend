package com.example.orders;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductsRepository {

	@Insert("""
			INSERT INTO product(name, description, cost)
			VALUES(#{name}, #{description}, #{cost})
			""")
	void saveProduct(String name, String description, BigDecimal cost);

	@Select("""
			SELECT id, name, cost
			FROM product
			ORDER BY RAND()
			LIMIT #{productsCount}
			""")
	List<Product> findRandomProducts(int productsCount);

}
