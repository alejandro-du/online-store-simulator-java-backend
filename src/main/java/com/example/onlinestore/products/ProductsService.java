package com.example.onlinestore.products;

import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class ProductsService {

	private final ProductsRepository productsRepository;

	private static final Faker faker = new Faker();

	@PostMapping(value = "/demo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Product> createDemoData(int count, int minPrice, int maxPrice) {
		// TODO: validate input

		return Flux.range(0, count)
				.map(productNumber -> new Product(
						null,
						faker.book().title() + " " + faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
						new BigDecimal(faker.random().nextInt(minPrice, maxPrice))))
				.flatMap(product -> productsRepository.save(product).flatMap(id -> Mono.just(product)));
	}

	@GetMapping(value = "")
	public Mono<Product> findRandomProduct() {
		return productsRepository.findRandom();
	}

	@DeleteMapping("")
	public Mono<Long> deleteAll() {
		return productsRepository.deleteAll();
	}

}
