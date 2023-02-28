package com.study.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("product")
public class Product {

    @Id
    private String id;

    private String name;

    private Long price;


    public void changePrice(Long price) {
        this.price = price;
    }

}
