package com.example.onlinestore.orders;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class OrdersService {

	private final OrdersRepository ordersRepository;

	@RequestMapping(value = "/saveRandom", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Transactional
	public Mono<Order> saveRandom(int itemCount) {
		Order order = new Order(null, LocalDateTime.now());
		return ordersRepository.save(order)
				.doOnNext(count -> ordersRepository.saveRandomItems(order.getId(), itemCount).subscribe());
	}

	@RequestMapping(value = "/deleteAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Boolean> deleteAll() {
		return ordersRepository.deleteAll()
				.then(Mono.just(true));
	}

	@RequestMapping(value = "/count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Long> count() {
		return ordersRepository.count().timeout(Duration.ofSeconds(1)).onErrorResume(e -> Mono.empty());
	}

}
