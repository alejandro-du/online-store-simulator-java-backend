package com.example.onlinestore.simulation;

import java.time.Duration;
import java.util.function.Function;

import com.example.onlinestore.orders.OrdersService;
import com.example.onlinestore.products.ProductsService;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class SimulationService {

	@Data
	@AllArgsConstructor
	public static class SimulationResult {
		private long time;
		private long count;
	}

	private final ProductsService productsService;
	private final OrdersService ordersService;

	@GetMapping(value = "/orderCount")
	public Flux<Long> orderCount() {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> ordersService.count());
	}

	@GetMapping(value = "/productCount")
	public Flux<Long> productCount() {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> productsService.count());
	}

	@GetMapping(value = "/visits", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<SimulationResult> visits(int productVisitsPerMinute) {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> getFlux(productVisitsPerMinute, i -> productsService.findRandom()));
	}

	@GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<SimulationResult> orders(int ordersPerMinute, int itemsPerOrder) {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> getFlux(ordersPerMinute, i -> ordersService.saveRandom(itemsPerOrder)));
	}

	private Mono<SimulationResult> getFlux(int countPerMinute, Function<Integer, Publisher<?>> dbOperation) {
		double countPerSecond = countPerMinute / 60d;
		int count;

		if (countPerSecond < 1) {
			boolean generateEvent = Math.random() < countPerSecond;
			if (generateEvent) {
				count = 1;
			} else {
				count = 0;
				dbOperation = i -> Mono.just(1);
			}
		} else {
			count = (int) countPerSecond;
		}

		return Flux.range(0, count)
				.onBackpressureDrop()
				.flatMap(dbOperation)
				.count()
				.elapsed()
				.map(tuple -> new SimulationResult(tuple.getT1(), tuple.getT2()))
				.onErrorResume(e -> Mono.just(new SimulationResult(0, 0)));
	}

}
