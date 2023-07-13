package com.example.onlinestore.products;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class ProductsService {

	private static final Faker faker = new Faker();

	private final ProductsRepository productsRepository;

	@RequestMapping(value = "/create", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Product> create(int count, int minPrice, int maxPrice) {
		return Flux.range(0, count)
				.map(productNumber -> new Product(
						null,
						faker.book().title() + " " + faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
						new BigDecimal(faker.random().nextInt(minPrice, maxPrice))))
				.flatMap(productsRepository::save);
	}

	@RequestMapping(value = "/findRandom", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Product> findRandom() {
		return productsRepository.findRandom();
	}

	@RequestMapping(value = "/deleteAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Boolean> deleteAll() {
		return productsRepository.deleteAll()
				.then(Mono.just(true));
	}

	@RequestMapping(value = "/count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Long> count() {
		return productsRepository.count()
				.timeout(Duration.ofSeconds(1))
				.onErrorResume(e -> Mono.empty());
	}

}
