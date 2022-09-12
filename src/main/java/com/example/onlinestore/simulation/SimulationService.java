package com.example.onlinestore.simulation;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinestore.orders.OrdersService;
import com.example.onlinestore.products.ProductsService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class SimulationService {

	@Data
	@AllArgsConstructor
	public static class SimulationResult {
		private long time;
	}

	private final ProductsService productsService;
	private final OrdersService ordersService;

	@GetMapping(value = "/views", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<SimulationResult> views(int count, int intervalSeconds, int timeoutMillis) {
		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(intervalNumber -> Flux.range(0, count)
						.flatMap(productNumber -> productsService.findRandom())
						.elapsed()
						.map(Tuple2::getT1)
						.reduce(Math::max)
						.map(SimulationResult::new)
						.timeout(Duration.ofMillis(timeoutMillis), Mono.just(new SimulationResult(-1))));
	}

	@GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<SimulationResult> orders(int count, int itemsPerOrder, int intervalSeconds, int timeoutMillis) {
		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(intervalNumber -> Flux.range(0, count)
						.flatMap(orderNumber -> ordersService.saveRandom(itemsPerOrder))
						.elapsed()
						.map(Tuple2::getT1)
						.reduce(Math::max)
						.map(SimulationResult::new)
						.timeout(Duration.ofMillis(timeoutMillis), Mono.just(new SimulationResult(-1))));
	}

}
