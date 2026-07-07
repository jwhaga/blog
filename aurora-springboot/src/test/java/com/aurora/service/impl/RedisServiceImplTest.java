package com.aurora.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisServiceImpl 单元测试")
class RedisServiceImplTest {

    @InjectMocks
    private RedisServiceImpl redisService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    @Mock
    private SetOperations<String, Object> setOperations;
    @Mock
    private ListOperations<String, Object> listOperations;
    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private static final String TEST_KEY = "test:key";
    private static final String TEST_HASH_KEY = "hashField";
    private static final String TEST_VALUE = "testValue";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Nested
    @DisplayName("基础操作 (set/get/del)")
    class BasicOperations {

        @Test
        @DisplayName("set 应存储键值对")
        void shouldSetValue() {
            redisService.set(TEST_KEY, TEST_VALUE);
            verify(valueOperations).set(TEST_KEY, TEST_VALUE);
        }

        @Test
        @DisplayName("set 带过期时间应存储并设置过期")
        void shouldSetValueWithExpire() {
            redisService.set(TEST_KEY, TEST_VALUE, 100L);
            verify(valueOperations).set(TEST_KEY, TEST_VALUE, 100L, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("get 应返回存储的值")
        void shouldGetValue() {
            when(valueOperations.get(TEST_KEY)).thenReturn(TEST_VALUE);
            Object result = redisService.get(TEST_KEY);
            assertEquals(TEST_VALUE, result);
        }

        @Test
        @DisplayName("del 应删除键并返回成功")
        void shouldDeleteKey() {
            when(redisTemplate.delete(TEST_KEY)).thenReturn(true);
            Boolean result = redisService.del(TEST_KEY);
            assertTrue(result);
        }

        @Test
        @DisplayName("不存在的键 get 应返回 null")
        void shouldReturnNullWhenKeyNotExists() {
            when(valueOperations.get("non:existent")).thenReturn(null);
            Object result = redisService.get("non:existent");
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Hash 操作 (hSet/hGet/hDel)")
    class RedisHashOps {

        @Test
        @DisplayName("hSet 应存储哈希字段")
        void shouldSetHashField() {
            redisService.hSet(TEST_KEY, TEST_HASH_KEY, TEST_VALUE);
            verify(hashOperations).put(TEST_KEY, TEST_HASH_KEY, TEST_VALUE);
        }

        @Test
        @DisplayName("hSet 带过期时间应存储并设置过期")
        void shouldSetHashFieldWithExpire() {
            when(redisTemplate.expire(TEST_KEY, 100L, TimeUnit.SECONDS)).thenReturn(true);

            Boolean result = redisService.hSet(TEST_KEY, TEST_HASH_KEY, TEST_VALUE, 100L);

            assertTrue(result);
            verify(hashOperations).put(TEST_KEY, TEST_HASH_KEY, TEST_VALUE);
            verify(redisTemplate).expire(TEST_KEY, 100L, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("hGet 应返回哈希字段值")
        void shouldGetHashField() {
            when(hashOperations.get(TEST_KEY, TEST_HASH_KEY)).thenReturn(TEST_VALUE);
            Object result = redisService.hGet(TEST_KEY, TEST_HASH_KEY);
            assertEquals(TEST_VALUE, result);
        }

        @Test
        @DisplayName("hDel 应删除哈希字段")
        void shouldDeleteHashField() {
            redisService.hDel(TEST_KEY, TEST_HASH_KEY);
            verify(hashOperations).delete(TEST_KEY, TEST_HASH_KEY);
        }

        @Test
        @DisplayName("hHasKey 应检查哈希字段是否存在")
        void shouldCheckHashFieldExists() {
            when(hashOperations.hasKey(TEST_KEY, TEST_HASH_KEY)).thenReturn(true);
            Boolean result = redisService.hHasKey(TEST_KEY, TEST_HASH_KEY);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Set 操作 (sAdd/sMembers/sIsMember)")
    class RedisSetOps {

        @Test
        @DisplayName("sAdd 应添加元素到集合")
        void shouldAddToSet() {
            when(setOperations.add(TEST_KEY, "a", "b")).thenReturn(2L);
            Long result = redisService.sAdd(TEST_KEY, "a", "b");
            assertEquals(2L, result);
        }

        @Test
        @DisplayName("sMembers 应返回集合所有元素")
        void shouldGetSetMembers() {
            Set<Object> expected = Set.of("a", "b", "c");
            when(setOperations.members(TEST_KEY)).thenReturn(expected);
            Set<Object> result = redisService.sMembers(TEST_KEY);
            assertEquals(3, result.size());
            assertTrue(result.contains("a"));
        }

        @Test
        @DisplayName("sIsMember 应判断元素是否在集合中")
        void shouldCheckSetMembership() {
            when(setOperations.isMember(TEST_KEY, "a")).thenReturn(true);
            Boolean result = redisService.sIsMember(TEST_KEY, "a");
            assertTrue(result);
        }

        @Test
        @DisplayName("sRemove 应从集合中移除元素")
        void shouldRemoveFromSet() {
            when(setOperations.remove(TEST_KEY, "a")).thenReturn(1L);
            Long result = redisService.sRemove(TEST_KEY, "a");
            assertEquals(1L, result);
        }
    }

    @Nested
    @DisplayName("数值操作 (incr/decr/incrExpire)")
    class NumericOperations {

        @Test
        @DisplayName("incr 应自增并返回新值")
        void shouldIncrement() {
            when(valueOperations.increment(TEST_KEY, 1)).thenReturn(5L);
            Long result = redisService.incr(TEST_KEY, 1);
            assertEquals(5L, result);
        }

        @Test
        @DisplayName("decr 应自减并返回新值")
        void shouldDecrement() {
            when(valueOperations.increment(TEST_KEY, -2)).thenReturn(3L);
            Long result = redisService.decr(TEST_KEY, 2);
            assertEquals(3L, result);
        }

        @Test
        @DisplayName("incrExpire 首次自增时应设置过期时间")
        void shouldSetExpireOnFirstIncrement() {
            when(valueOperations.increment(TEST_KEY, 1)).thenReturn(1L);
            when(redisTemplate.expire(TEST_KEY, 100L, TimeUnit.SECONDS)).thenReturn(true);

            Long result = redisService.incrExpire(TEST_KEY, 100L);

            assertEquals(1L, result);
            verify(redisTemplate).expire(TEST_KEY, 100L, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("incrExpire 非首次自增时不设置过期时间")
        void shouldNotSetExpireOnSubsequentIncrement() {
            when(valueOperations.increment(TEST_KEY, 1)).thenReturn(5L);

            Long result = redisService.incrExpire(TEST_KEY, 100L);

            assertEquals(5L, result);
            verify(redisTemplate, never()).expire(anyString(), anyLong(), any());
        }
    }

    @Nested
    @DisplayName("键操作 (expire/hasKey)")
    class KeyOperations {

        @Test
        @DisplayName("expire 应设置键过期时间")
        void shouldSetExpire() {
            when(redisTemplate.expire(TEST_KEY, 60L, TimeUnit.SECONDS)).thenReturn(true);
            Boolean result = redisService.expire(TEST_KEY, 60L);
            assertTrue(result);
        }

        @Test
        @DisplayName("hasKey 应检查键是否存在")
        void shouldCheckKeyExists() {
            when(redisTemplate.hasKey(TEST_KEY)).thenReturn(true);
            Boolean result = redisService.hasKey(TEST_KEY);
            assertTrue(result);
        }
    }

}
