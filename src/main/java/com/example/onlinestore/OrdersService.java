package com.example.onlinestore;

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
		private int orderCount = 0;
		private int itemCount = 0;
		private int disappointedVisitors = 0;
		private BigDecimal missedOpportunityTotal = BigDecimal.ZERO;
		private long averageWaitTimeMillis = 0;
		private long maxWaitTimeMillis = 0;
		private long simulationTimeMillis = 0;
	}

	@PostMapping("/random")
	public SimulationSummary simulate(int visitors, float conversionRate, int productViewsPerVisitor,
			int maxWaitingTimeMillis, int itemsPerOrder, BigDecimal maxBudgetPerVisitor) {

		long simulationStartTime = System.currentTimeMillis();
		var simulationSummary = new SimulationSummary();

		if (visitors < 0 ||
				conversionRate < 0 || conversionRate > 1 ||
				productViewsPerVisitor < 0 || productViewsPerVisitor < itemsPerOrder ||
				maxWaitingTimeMillis < 0 ||
				itemsPerOrder <= 0 ||
				maxBudgetPerVisitor.compareTo(BigDecimal.ZERO) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < visitors; i++) {
			var visited = new ArrayList<Product>();
			boolean leaveTheStoreDisappointed = false;

			for (int j = 0; j < productViewsPerVisitor; j++) {
				Product product = productsService.find();
				long endTime = System.currentTimeMillis();
				long waitInMillis = (endTime - startTime);
				if (waitInMillis > maxWaitingTimeMillis) {
					leaveTheStoreDisappointed = true;
					break;
				} else {
					visited.add(product);
				}
			}

			long endTime = System.currentTimeMillis();
			long waitInMillis = endTime - startTime;
			simulationSummary.setAverageWaitTimeMillis((long) (simulationSummary.getAverageWaitTimeMillis() + (double)(waitInMillis / visitors)));
			if(waitInMillis > simulationSummary.getMaxWaitTimeMillis()) {
				simulationSummary.setMaxWaitTimeMillis(waitInMillis);
			}

			boolean visitorConverted = random.nextDouble() <= conversionRate;

			if (leaveTheStoreDisappointed) {
				simulationSummary.setDisappointedVisitors(simulationSummary.getDisappointedVisitors() + 1);
				if (visitorConverted) {
					simulationSummary.setMissedOpportunityTotal(
							simulationSummary.getMissedOpportunityTotal().add(maxBudgetPerVisitor));
				}
				continue; // with the next visitor
			} else if (visitorConverted) {
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

			long simulationEndTime = System.currentTimeMillis();
			long simulationTime = (simulationEndTime - simulationStartTime);
			simulationSummary.setSimulationTimeMillis(simulationTime);
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
