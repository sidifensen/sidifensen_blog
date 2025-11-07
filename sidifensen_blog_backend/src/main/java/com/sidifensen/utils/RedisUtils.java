package com.sidifensen.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return true;
        } else {
            throw new RuntimeException("超时时间小于0");
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @return
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.expire(key, time, timeUnit);
            return true;
        } else {
            throw new RuntimeException("超时时间小于0");
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key      键 不能为null
     * @param tiemtype 时间类型
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key, TimeUnit tiemtype) {

        return redisTemplate.getExpire(key, tiemtype);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取多个key的值
     *
     * @param keys 键列表
     * @return 对应的值列表
     */
    public List<Object> multiGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        return true;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            this.set(key, value);
        }
        return true;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  time 时间类型自定义设定
     * @return true成功 false 失败
     */

    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } else {
            this.set(key, value);
        }
        return true;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key
     * @param delta 要减少几(大于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键
     * @param item 项 不能为null
     * @return
     */
    public Object hget(String key, Object item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的value
     *
     * @param key 键
     * @return value
     */
    public Object hmget(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 批量获取hash中的多个字段值
     *
     * @param key      键
     * @param hashKeys 字段列表
     * @return 对应的值列表
     */
    public List hmultiGet(String key, List hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 获取hashKey对应的所有值
     *
     * @param key 键
     * @return 对应的多个值
     */
    public List hmgetValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<Object, Object> map, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @param time    时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, Object hashKey, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return true;
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        final Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0)
            expire(key, time);
        return count;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        final Long count = redisTemplate.opsForSet().remove(key, values);
        return count;
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSetList(String key, List<Object> value) {
        redisTemplate.opsForList().rightPushAll(key, value);
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSetList(String key, List<Object> value, long time) {
        redisTemplate.opsForList().rightPushAll(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return true;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
        return true;
    }

    // ==================== ZSet（有序集合）相关操作 ====================

    /**
     * 向有序集合添加一个成员，或者更新已存在成员的分数
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 增加有序集合中成员的分数
     *
     * @param key   键
     * @param value 值
     * @param delta 增量
     * @return 增加后的分数
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 获取有序集合中指定成员的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, String value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 批量获取有序集合中多个成员的分数（使用Pipeline优化性能）
     *
     * @param key    键
     * @param values 成员列表
     * @return 分数列表（与values列表顺序对应）
     */
    public List<Object> zScoreBatch(String key, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return List.of();
        }

        // 使用Pipeline批量执行命令，减少网络往返次数
        // Pipeline会自动收集每个命令的返回值
        return redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) {
                for (String value : values) {
                    operations.opsForZSet().score(key, value);
                }
                return null;
            }
        });
    }

    /**
     * 获取有序集合指定范围内的成员（按分数从小到大）
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 成员列表
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取有序集合指定范围内的成员（按分数从大到小）
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 成员列表
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取有序集合指定分数范围内的成员（按分数从小到大）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 成员列表
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取有序集合的成员数量
     *
     * @param key 键
     * @return 成员数量
     */
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 移除有序集合中的一个或多个成员
     *
     * @param key    键
     * @param values 值
     * @return 成功移除的成员数量
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 移除有序集合中指定排名范围内的所有成员
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 移除的成员数量
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 移除有序集合中指定分数范围内的所有成员
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 移除的成员数量
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    // ==================== Key 操作 ====================

    /**
     * 重命名key（原子操作）
     * 如果newKey已存在，会被覆盖
     *
     * @param oldKey 原key
     * @param newKey 新key
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 根据模式删除匹配的所有key
     * 注意：在生产环境中使用keys命令可能会影响性能，建议谨慎使用
     *
     * @param pattern 匹配模式，如：sidifensen_blog:RateLimit:*:user:123
     * @return 删除的key数量
     */
    public long deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            Long deletedCount = redisTemplate.delete(keys);
            return deletedCount != null ? deletedCount : 0;
        }
        return 0;
    }

}
