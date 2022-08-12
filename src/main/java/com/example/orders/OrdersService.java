package com.example.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class OrdersService {

	private static final Random random = new Random(System.currentTimeMillis());

	private final OrdersRepository ordersRepository;
	private final ProductsService productsService;

	@Data
	public static class SimulationSummary {
		private BigDecimal salesTotal = BigDecimal.ZERO;
		private int orderCount;
		private int itemCount;
		private int disappointedVisitors;
		private long processingTimeMillis;
	}

	@PostMapping("/random")
	public SimulationSummary simulate(int visitors, float conversionRate, int productViewsPerVisitor,
			int maxWaitingTimeSeconds, int itemsPerOrder, BigDecimal maxBudgetPerVisitor) {

		var simulationSummary = new SimulationSummary();

		if (visitors < 0 ||
				conversionRate < 0 || conversionRate > 1 ||
				productViewsPerVisitor < 0 || productViewsPerVisitor < itemsPerOrder ||
				maxWaitingTimeSeconds < 0 ||
				itemsPerOrder <= 0 ||
				maxBudgetPerVisitor.compareTo(BigDecimal.ZERO) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		long processingStartTime = System.currentTimeMillis();

		for (int i = 0; i < visitors; i++) {
			var visited = new ArrayList<Product>();
			boolean leaveTheStoreDisappointed = false;

			for (int j = 0; j < productViewsPerVisitor; j++) {
				long startTime = System.currentTimeMillis();
				Product product = productsService.find();
				long endTime = System.currentTimeMillis();
				long waitInSeconds = (endTime - startTime) / 1000;
				if (waitInSeconds > maxWaitingTimeSeconds) {
					leaveTheStoreDisappointed = true;
					break;
				} else {
					visited.add(product);
				}
			}

			if (leaveTheStoreDisappointed) {
				simulationSummary.setDisappointedVisitors(simulationSummary.getDisappointedVisitors() + 1);
				continue; // with the next visitor
			}

			if (random.nextDouble() <= conversionRate) {
				var itemsToBuy = new HashSet<Product>();
				var moneyLeft = maxBudgetPerVisitor;
				for (int k = 0; k < visited.size() && itemsToBuy.size() < itemsPerOrder; k++) {
					var item = visited.get(k);
					if (moneyLeft.compareTo(item.getCost()) >= 0) {
						if (itemsToBuy.add(item)) {
							moneyLeft = moneyLeft.subtract(item.getCost());
						}
					}
				}

				if (itemsToBuy.size() > 0) {
					Order order = placeOrder(itemsToBuy);
					simulationSummary.setSalesTotal(simulationSummary.getSalesTotal().add(order.getTotal()));
					simulationSummary.setOrderCount(simulationSummary.getOrderCount() + 1);
					simulationSummary.setItemCount(simulationSummary.getItemCount() + order.getItems().size());
				}
			}

			long processingEndTime = System.currentTimeMillis();
			long processingTime = (processingEndTime - processingStartTime);
			simulationSummary.setProcessingTimeMillis(processingTime);
		}

		return simulationSummary;
	}

	@Transactional
	private Order placeOrder(Collection<Product> items) {
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
