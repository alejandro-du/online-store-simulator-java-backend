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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class SimulationService {

	private final ProductsService productsService;
	private final OrdersService ordersService;

	@GetMapping(value = "/views", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Long> views(int count, int intervalSeconds, int timeoutMillis) {
		Flux<Object> views = Flux.range(0, count)
				.flatMap(productNumber -> productsService.findRandomProduct());

		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(l -> timeCounter(views, timeoutMillis));
	}

	@GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Long> orders(int count, int itemsPerOrder, int intervalSeconds, int timeoutMillis) {
		Flux<Object> orders = Flux.range(0, count)
				.flatMap(orderNumber -> ordersService.saveRandom(itemsPerOrder));

		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(l -> timeCounter(orders, timeoutMillis));
	}

	private Mono<Long> timeCounter(Flux<Object> flux, int timeout) {
		return flux
				.timeout(Duration.ofSeconds(timeout))
				.elapsed()
				.map(Tuple2::getT1)
				.reduce((initial, accumulator) -> accumulator += initial)
				.onErrorReturn(null);
	}

}
