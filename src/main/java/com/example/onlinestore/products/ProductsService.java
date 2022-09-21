package com.example.onlinestore.products;

import java.math.BigDecimal;

import com.github.javafaker.Faker;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class ProductsService {

	private static final Faker faker = new Faker();

	private final ProductsRepository productsRepository;

	@RequestMapping(value = "/createDemoData", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Product> createDemoData(int count, int minPrice, int maxPrice) {
		return Flux.range(0, count)
				.map(productNumber -> new Product(
						null,
						faker.book().title() + " " + faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
						new BigDecimal(faker.random().nextInt(minPrice, maxPrice))))
				.flatMap(product -> productsRepository.save(product).flatMap(id -> Mono.just(product)));
	}

	@RequestMapping(value = "/findRandom", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Product> findRandom() {
		return productsRepository.findRandom();
	}

	@RequestMapping(value = "/deleteAll", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Void> deleteAll() {
		return productsRepository.deleteAll();
	}

	@RequestMapping(value = "/count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<Long> count() {
		return productsRepository.count();
	}

}
