package com.example.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class OrdersService {

	private static final Random random = new Random(System.currentTimeMillis());

	private final OrdersRepository ordersRepository;
	private final ProductsService productsService;

	@PostMapping("/random")
	public BigDecimal simulate(int visitors, float conversionRate, int productViewsPerVisitor,
			int maxWaitingTimeSeconds,
			int minItemsPerOrder,
			int maxItemsPerOrder) {

		BigDecimal total = BigDecimal.ZERO;

		if (minItemsPerOrder > maxItemsPerOrder || productViewsPerVisitor < minItemsPerOrder) {
			return total;
		}

		for (int i = 0; i < visitors; i++) {
			long startTime = System.currentTimeMillis();
			List<Product> items = productsService.find(productViewsPerVisitor);
			long endTime = System.currentTimeMillis();
			long waitInSeconds = (endTime - startTime) / 1000;
			if (waitInSeconds <= maxWaitingTimeSeconds) {
				if (random.nextFloat(0, 1) <= conversionRate) {
					int itemsCount = random.nextInt(minItemsPerOrder, maxItemsPerOrder + 1);
					Order order = placeOrder(items.subList(items.size() - itemsCount, items.size()));
					total = total.add(order.getTotal());
				}
			}
		}

		return total;
	}

	@Transactional
	private Order placeOrder(List<Product> items) {
		Order newOrder = new Order(null, LocalDateTime.now(), items);
		ordersRepository.save(newOrder);
		ordersRepository.saveItems(newOrder.getId(), items);
		return newOrder;
	}

	@DeleteMapping("/")
	public void deleteAll() {
		ordersRepository.deleteAll();
	}

	@GetMapping("/total")
	public BigDecimal getTotalSales(String start, String end) {
		return ordersRepository.getTotal(start, end);
	}

}
