package com.example.orders;

import java.util.Collection;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrdersRepository {

	@Insert("INSERT INTO order_(time) VALUES(#{time});")
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	Long save(Order order);

	@Insert("""
			<script>
			INSERT INTO order_item(order_id, product_id) VALUES
			<foreach item="item" collection="items" separator=",">
				(#{orderId}, #{item.id})
			</foreach>
			</script>
			""")
	Long saveItems(Long orderId, Collection<Product> items);

	@Delete("DELETE FROM order_")
	void deleteAll();

}
