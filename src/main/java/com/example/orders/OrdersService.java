package com.example.orders;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.springframework.transaction.annotation.Transactional;
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

	@RequestMapping("/order")
	@Transactional
	public long order(int itemsCount) throws InterruptedException, ExecutionException {
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

	@RequestMapping("/simulate")
	public void simulate(int visitors, float conversionRate, int productViewsPerVisitor, int maxWaitingTime,
			int minItemsPerOrder,
			int maxItemsPerOrder) {

		long startTime = System.currentTimeMillis();
		IntStream.range(0, visitors)
				.forEach(i -> {
					try {
						productsService.find(productViewsPerVisitor);
						long endTime = System.currentTimeMillis();
						long waitInSeconds = (endTime - startTime) / 1000;

						if (waitInSeconds <= maxWaitingTime) {
							if (random.nextFloat(0, 1) <= conversionRate) {
								int itemsCount = random.nextInt(minItemsPerOrder, maxItemsPerOrder + 1);
								order(itemsCount);
							}
						}
					} catch (InterruptedException | ExecutionException ignored) {
					}
				});
	}

	@RequestMapping("/deleteAll")
	public void deleteAll() {
		ordersRepository.deleteAll();
	}

}
