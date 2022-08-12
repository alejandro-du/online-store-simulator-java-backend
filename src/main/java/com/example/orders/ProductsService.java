package com.example.orders;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(originPatterns = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
@RequiredArgsConstructor
public class ProductsService {

	private final ProductsRepository productsRepository;

	@PostMapping("/demo")
	public void generate(int count, int minPrice, int maxPrice) {
		// TODO: validate input
		
		IntStream.range(0, count)
				.parallel()
				.forEach(i -> {
					Faker faker = new Faker();
					String name = faker.book().title() + " "
							+ faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
					String description = faker.lorem().characters(500, 5000);
					BigDecimal cost = new BigDecimal(faker.random().nextInt(minPrice, maxPrice));
					productsRepository.save(name, description, cost);
				});
	}

	@GetMapping("/random")
	public Product find() {
		return productsRepository.findRandom();
	}

	@DeleteMapping("/")
	public void deleteAll() {
		productsRepository.deleteAll();
	}

}
