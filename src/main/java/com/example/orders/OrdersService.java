package com.example.orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersService {

	private static final Random random = new Random(System.currentTimeMillis());

	private final OrdersRepository ordersRepository;
	private final ProductsService productsService;

	@PostMapping("/random")
	@Transactional
	public Order placeOrder(int itemsCount) throws InterruptedException, ExecutionException {
		List<Product> items = productsService.find(itemsCount).get();
		Order newOrder = new Order(null, LocalDateTime.now(), items);
		ordersRepository.save(newOrder);
		ordersRepository.saveItems(newOrder.getId(), newOrder.getItems());
		return newOrder;
	}

	@PostMapping("/random/bulk")
	public void simulate(int visitors, float conversionRate, int productViewsPerVisitor, int maxWaitingTimeSeconds,
			int minItemsPerOrder,
			int maxItemsPerOrder) {

		for (int i = 0; i < visitors; i++) {
			long startTime = System.currentTimeMillis();
			productsService.find(productViewsPerVisitor).thenAccept(products -> {
				try {
					long endTime = System.currentTimeMillis();
					long waitInSeconds = (endTime - startTime) / 1000;
					if (waitInSeconds <= maxWaitingTimeSeconds) {
						if (random.nextFloat(0, 1) <= conversionRate) {
							int itemsCount = random.nextInt(minItemsPerOrder, maxItemsPerOrder + 1);
							placeOrder(itemsCount);
						}
					}
				} catch (InterruptedException | ExecutionException ignored) {
				}
			});
		}
	}

	@DeleteMapping("/")
	public void deleteAll() {
		ordersRepository.deleteAll();
	}

}
