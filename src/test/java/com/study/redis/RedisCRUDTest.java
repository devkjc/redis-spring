package com.study.redis;

import com.study.redis.domain.Product;
import com.study.redis.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback(value = false)
public class RedisCRUDTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("P0001", "테스트_상품", 20000L);
    }

    @AfterEach
    void teardown() {
        productRepository.deleteById(product.getId());
    }

    @Test
    @DisplayName("Redis 에 데이터를 저장하면 정상적으로 조회되어야 한다")
    void redis_save_test() {
        // given
        productRepository.save(product);

        // when
        Product persistProduct = productRepository.findById(product.getId())
                .orElseThrow(RuntimeException::new);

        // then
        assertThat(persistProduct.getId()).isEqualTo(product.getId());
        assertThat(persistProduct.getName()).isEqualTo(product.getName());
        assertThat(persistProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    @DisplayName("Redis 에 데이터를 수정하면 정상적으로 수정되어야 한다")
    void redis_update_test() {
        // given
        productRepository.save(product);
        Product persistProduct = productRepository.findById(product.getId())
                .orElseThrow(RuntimeException::new);

        // when
        persistProduct.changePrice(35000L);
        productRepository.save(persistProduct);

        // then
        assertThat(persistProduct.getPrice()).isEqualTo(35000L);
    }

    @Test
    @DisplayName("Redis 에 데이터를 삭제하면 정상적으로 삭제되어야 한다")
    void redis_delete_test() {
        // given
        productRepository.save(product);

        // when
        productRepository.delete(product);
        Optional<Product> deletedProduct = productRepository.findById(product.getId());

        // then
        assertTrue(deletedProduct.isEmpty());
    }

}
