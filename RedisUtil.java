package com.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hexiaoshu
 * @Description: redis常用操作工具类  使用 StringRedisTemplate
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public StringRedisTemplate getstringRedisTemplate() {
        return this.stringRedisTemplate;
    }

    /*stringRedisTemplate.opsForValue();//操作字符串
    stringRedisTemplate.opsForHash();//操作hash
    stringRedisTemplate.opsForList();//操作list
    stringRedisTemplate.opsForSet();//操作set
    stringRedisTemplate.opsForZSet();//操作有序set*/

    // -------------------key相关操作--------------------- //
    /**
     * 获取指定key 的值
     * @param key key
     * @return String value
     */
    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置 key 的过期时间 /1 秒  2分  3小时
     * @return Boolean
     */
    public Boolean expire(String key,long time,Integer type){
        if (type==1){
            return stringRedisTemplate.expire(key,time,TimeUnit.SECONDS);
        }
        if (type==2){
            return stringRedisTemplate.expire(key,time,TimeUnit.MINUTES);
        }
        if (type==3){
            return stringRedisTemplate.expire(key,time,TimeUnit.HOURS);
        }
        return false;
    }

    /**
     * 删除key
     * @param key key
     */
    public void delete(String key) { stringRedisTemplate.delete(key); }

    /**
     * 批量删除key
     * @param keys Collection<String> keys
     */
    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * 是否存在key
     * @param key key
     * @return Boolean
     */
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     * @param key key
     * @return Boolean
     */
    public Boolean persist(String key) {
        return stringRedisTemplate.persist(key);
    }

    /**
     * 模糊查询key
     * @param pattern 匹配字符
     * @return keys
     */
    public Set<String> keys(String pattern){
        return stringRedisTemplate.keys(pattern);
    }

    // -------------------String 相关操作--------------------- //

    /**
     * 普通存储 String
     * @param key   key
     * @param value value
     */
    public void set(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    /**
     * 存储 String，选择存储db，以及设置失效时间
     * @param key   key
     * @param value value
     * @param db    db
     * @param timeOut timeOut /分钟
     */
    public void setDb(String key,String value,Integer db,long timeOut){
        stringRedisTemplate.opsForValue().set(key,value,timeOut,TimeUnit.MINUTES);
        stringRedisTemplate.move(key,db);
    }


    /**
     * 普通存储 String，附带设置 失效时间
     * @param key   key
     * @param value value
     * @param timeOut 失效时间 秒
     */
    public void set(String key,String value, long timeOut){
        stringRedisTemplate.opsForValue().set(key, value,timeOut, TimeUnit.SECONDS);
    }

    /**
     * 批量添加
     * @param maps maps
     */
    public void multiSet(Map<String, String> maps) {
        stringRedisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 批量获取
     * @param keys keys
     * @return List<String>
     */
    public List<String> multiGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     * @param key   key
     * @param value value
     * @return 之前已经存在返回false,不存在返回true
     */
    public boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 获取字符串的长度
     * @param key key
     * @return
     */
    public Long size(String key) {
        return stringRedisTemplate.opsForValue().size(key);
    }

    // ------------------- 自增(或自减) -------------//
    /**
     * 增加(自增长), 负数则为自减
     * @param key key
     * @return
     */
    public Long incrBy(String key, long increment) {
        return stringRedisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * @param key key
     * @return
     */
    public Double incrByFloat(String key, double increment) {
        return stringRedisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * @param key       key   eg:"user" 对象
     * @param field     field 对应字段 eg: age , 自增 1
     * @param increment increment 自增多少 long 类型
     * @return
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }
    
    // ------------------- bitmap 位图操作 -------------//

    public Boolean setBit(String key, Integer index, Boolean tag) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) con -> con.setBit(key.getBytes(), index, tag));
    }

    public Boolean getBit(String key, Integer index) {
        return stringRedisTemplate.execute((RedisCallback<Boolean>) con -> con.getBit(key.getBytes(), index));
    }

    /**
     * 统计bitmap中，value为1的个数，非常适用于统计网站的每日活跃用户数等类似的场景
     *
     * @param key
     * @return
     */
    public Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    public Long bitCount(String key, int start, int end) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
    }

    public Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
        byte[][] bytes = new byte[desKey.length][];
        for (int i = 0; i < desKey.length; i++) {
            bytes[i] = desKey[i].getBytes();
        }
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
    }

    // ------------------------------------------------//


    // ------------------- Hash 操作 -------------//

    /**
     * 存储hash
     * @param key
     * @param map
     */
    public void Hmset(String key,Map<String, String> map){
        stringRedisTemplate.opsForHash().putAll(key,map);
    }

    /**
     * 获取所有 k-v
     * @param key
     * @return
     */
    public Map<Object, Object> HmgetAll(String key){
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有 key
     * @param key
     * @return
     */
    public Set<Object> HmKeys(String key){
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取所有value
     * @param key
     * @return
     */
    public List<Object> HmValues(String key){
        return stringRedisTemplate.opsForHash().values(key);
    }


    /**
     * 获取存储在哈希表中指定字段的值
     * @param key   key    "user"   eg: 存的user 对象的 name 的value 值
     * @param field field  "name"
     * @return
     */
    public Object hGet(String key, String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 删除一个或多个哈希表字段
     * @param key    key    "user" 用户对象
     * @param fields fields "name"
     * @return
     */
    public Long hDelete(String key, Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     * @param key   key    "user" 用户对象
     * @param field fields "name" 是否存在name
     * @return
     */
    public boolean hExists(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    // ------------------------list相关操作---------------------------- //

    /**
     * 存储在list头部，从 左开始存
     * @param key   key
     * @param value value
     * @return
     */
    public Long lLeftPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * @param key   key
     * @param value value
     * @return
     */
    public Long lLeftPushAll(String key, String... value) {
        return stringRedisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * @param key   key
     * @param value value  Collection<String> value
     * @return
     */
    public Long lLeftPushAll(String key, Collection<String> value) {
        return stringRedisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 弹出集合所有元素
     * @param key
     * @return
     */
    public List<String> lRangeAll(String key){
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 当list存在的时候才加入
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushIfPresent(String key, String value) {
        return stringRedisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * @param key   key
     * @param value value
     * @return 从右 添加
     */
    public Long lRightPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     *
     * @param key   key
     * @param value value..
     * @return
     */
    public Long lRightPushAll(String key, String... value) {
        return stringRedisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     *
     * @param key   key
     * @param value value Collection<String> value
     * @return
     */
    public Long lRightPushAll(String key, Collection<String> value) {
        return stringRedisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 移出并获取列表的第一个元素
     * @param key key
     * @return 删除的元素
     */
    public String lLeftPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key key
     * @param timeout 等待时间
     * @param unit  时间单位
     * @return
     */
    public String lBLeftPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移除并获取列表最后一个元素
     * @param key
     * @return 删除的元素
     */
    public String lRightPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key
     * @param timeout 等待时间
     * @param unit 时间单位
     * @return
     */
    public String lBRightPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().rightPop(key, timeout, unit);
    }
/** --------------------set相关操作-------------------------- */

    /**
     * set添加元素, 不允许重复  添加重复的value，会覆盖
     * @param key    key
     * @param values values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     * @param key
     * @return
     */
    public String sPop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, String value, String destKey) {
        return stringRedisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的大小
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合所有元素
     *
     * @param key key
     * @return
     */
    public Set<String> setMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     * @param key key
     * @return
     */
    public String sRandomMember(String key) {
        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中count个元素
     * @param key
     * @param count
     * @return
     */
    public List<String> sRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     * @param key
     * @param count
     * @return
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**------------------zSet相关操作--------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<String>> values) {
        return stringRedisTemplate.opsForZSet().add(key, values);
    }

    /**
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素, 从小到大排序
     * @param key
     * @param start 开始位置
     * @param end    结束位置, -1查询所有
     * @return
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合元素, 并且把score值也获取
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, long start,long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     * @param key
     * @param min 最小值
     * @param max 最大值
     *
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }


}
