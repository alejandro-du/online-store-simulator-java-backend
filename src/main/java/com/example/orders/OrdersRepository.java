package com.example.orders;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrdersRepository {

	@Insert("""
			INSERT INTO order_(time)
			VALUES(#{time})
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	long save(Order order);

	@Insert("""
			INSERT INTO order_item(order_id, product_id)
			VALUES(#{orderId}, #{productId})
			""")
	void saveItem(long orderId, int productId);

	@Delete("""
			DELETE FROM order_
			""")
	void deleteAll();

}
