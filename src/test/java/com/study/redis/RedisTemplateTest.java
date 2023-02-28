package com.study.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    String stringKey = "testKey";
    String listkey = "testList";
    String setKey = "testSet";
    String sortedSetKey = "testSorted";
    String hashKey = "testHash";

    String[] strings = {"H", "e", "l", "l", "o"};

    @AfterEach
    void teardown() {
        redisTemplate.delete(Arrays.asList(stringKey, listkey, setKey, sortedSetKey, hashKey));
    }

    @Test
    void stringRedis() {

        //given
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(stringKey, "1");

        //when
        valueOperations.increment(stringKey);
        int parseInt = Integer.parseInt(valueOperations.get(stringKey));
        //then
        Assertions.assertEquals(2, parseInt);
    }

    @Test
    public void listRedis() {

        //given
        ListOperations<String, String> operations = redisTemplate.opsForList();

        for (String string : strings) {
            operations.rightPush(listkey, string);
        }

        //when
        Long size = operations.size(listkey);
        List<String> range = operations.range(listkey, 0, -1);

        //then
        Assertions.assertEquals(size, 5);
        assertThat(range).containsExactly(strings);
    }

    @Test
    void setRedis() {
        //given
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        for (String string : strings) {
            operations.add(setKey, string);
        }
        //when
        Set<String> members = operations.members(setKey);
        Long size = operations.size(setKey);
        //then
        assertThat(members).containsOnly(strings);
        assertThat(size).isEqualTo(strings.length - 1);
    }

    @Test
    void sortedSetRedis() {
        //given
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        int score = 0;
        for (String string : strings) {
            operations.add(sortedSetKey, string, score++);
        }
        //when
        Set<String> range = operations.range(sortedSetKey, 0, -1);
        Long size = operations.size(sortedSetKey);

        //then
        assertThat(size).isEqualTo(strings.length - 1);
        assertThat(range).containsExactly("H","e","l","o");
    }

    @Test
    void hashRedis() {
        //given
        HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();

        int count = 0;

        for (String string : strings) {
            String countString = String.valueOf(count++);
            operations.put(hashKey, countString , string);
        }

        //when
        String value = (String) operations.get(hashKey, "0");
        Long size = operations.size(hashKey);
        //then
        assertThat(value).isEqualTo("H");
        assertThat(size).isEqualTo(strings.length);
    }
}
