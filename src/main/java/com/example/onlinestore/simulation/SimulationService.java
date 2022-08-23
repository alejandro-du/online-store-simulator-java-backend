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
	public Flux<Long> views(int productViews, int intervalSeconds) {
		Flux<Object> views = Flux.range(0, productViews)
				.flatMap(productNumber -> productsService.findRandomProduct());

		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(l -> timeCounter(views, intervalSeconds));
	}

	@GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Long> orders(int orderCount, int itemCount, int intervalSeconds) {
		Flux<Object> orders = Flux.range(0, orderCount)
				.flatMap(orderNumber -> ordersService.saveRandom(itemCount));

		return Flux.interval(Duration.ofSeconds(intervalSeconds))
				.flatMap(l -> timeCounter(orders, intervalSeconds));
	}

	private Mono<Long> timeCounter(Flux<Object> flux, int timeout) {
		return flux
				.timeout(Duration.ofSeconds(timeout))
				.elapsed()
				.map(Tuple2::getT1)
				.reduce((initial, accumulator) -> accumulator += initial)
				.onErrorReturn(-1l);
	}

}
