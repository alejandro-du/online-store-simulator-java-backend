package com.example.orders;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersService {

	private static final Random random = new Random(System.currentTimeMillis());

	private final OrdersRepository ordersRepository;
	private final ProductsService productsService;

	@RequestMapping(value = "/random", method = RequestMethod.POST)
	@Transactional
	public long placeOrder(int itemsCount) throws InterruptedException, ExecutionException {
		long orderId = saveOrder();
		saveItems(orderId, itemsCount);
		return orderId;
	}

	private long saveOrder() {
		Order order = new Order();
		order.setTime(LocalDateTime.now());
		ordersRepository.save(order);
		return order.getId();
	}

	private void saveItems(long orderId, int itemsCount) throws InterruptedException, ExecutionException {
		productsService.find(itemsCount).get().stream()
				.map(Product::getId)
				.forEach(
						productId -> ordersRepository.saveItem(orderId, productId));
	}

	@RequestMapping(value = "/random/bulk", method = RequestMethod.POST)
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

	@RequestMapping("/deleteAll") // TODO: fix HTTP methods here and elsewhere
	public void deleteAll() {
		ordersRepository.deleteAll();
	}

}
