package com.example.onlinestore.orders;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("")
	@Transactional
	public Mono<Long> saveRandom(int itemCount) {
		Order order = new Order(null, LocalDateTime.now());
		return ordersRepository.save(order)
				.flatMap(count -> ordersRepository.saveRandomItems(order.getId(), itemCount));
	}

	@DeleteMapping("")
	public Mono<Long> deleteAll() {
		return ordersRepository.deleteAll();
	}

}
