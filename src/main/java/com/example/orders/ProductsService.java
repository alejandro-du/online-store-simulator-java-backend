package com.example.orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsService {

	private final ProductsRepository productsRepository;

	@RequestMapping(value = "/demo", method = RequestMethod.POST)
	public void generate(int count, int minPrice, int maxPrice) {
		IntStream.range(0, count)
				.parallel()
				.forEach(i -> {
					Faker faker = new Faker();
					String name = faker.book().title() + faker.idNumber().toString();
					String description = faker.lorem().characters(500, 5000);
					BigDecimal cost = new BigDecimal(faker.random().nextInt(minPrice, maxPrice));
					productsRepository.saveProduct(name, description, cost);
				});
	}

	@RequestMapping(value = "/random", method = RequestMethod.GET)
	@Async
	public CompletableFuture<List<Product>> find(int count) {
		List<Product> products = productsRepository.findRandomProducts(count);
		return CompletableFuture.completedFuture(products);
	}

}
