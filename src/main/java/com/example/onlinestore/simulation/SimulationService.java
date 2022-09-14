package com.example.onlinestore.simulation;

import java.time.Duration;
import java.util.function.Function;

import org.reactivestreams.Publisher;
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
	public Flux<SimulationResult> views(int viewsPerMinute, int timeoutMillis) {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> getFlux(viewsPerMinute, timeoutMillis, i -> productsService.findRandom()));
	}

	@GetMapping(value = "/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<SimulationResult> orders(int ordersPerMinute, int itemsPerOrder, int timeoutMillis) {
		return Flux.interval(Duration.ofSeconds(1))
				.flatMap(intervalNumber -> getFlux(ordersPerMinute, timeoutMillis,
						i -> ordersService.saveRandom(itemsPerOrder)));
	}

	private Mono<SimulationResult> getFlux(int countPerMinute, int timeoutMillis,
			Function<Integer, Publisher<?>> mapper) {
		double ratePerSecond = countPerMinute / 60d;
		Flux<?> flux;

		if (ratePerSecond < 1) {
			if (Math.random() < ratePerSecond) {
				flux = Flux.range(0, 1)
						.flatMap(mapper);
			} else {
				flux = Flux.range(0, 1);
			}
		} else {
			flux = Flux.range(0, (int) ratePerSecond)
					.flatMap(mapper);
		}

		return flux
				.elapsed()
				.map(Tuple2::getT1)
				.reduce(Math::max)
				.map(SimulationResult::new)
				.timeout(Duration.ofMillis(timeoutMillis), Mono.just(new SimulationResult(-1)));
	}

}
