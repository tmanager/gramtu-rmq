package com.frank.gramturmq.redis;

/**
 * Redis操作类.
 *
 * @author 张孝党 2020/01/03.
 * @version V0.0.1.
 * <p>
 * 更新履历： V0.0.1 2020/01/03. 张孝党 创建.
 */
public class RedisService {
}
//@Slf4j
//@Service
//public class RedisService {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    /**
//     * 往redis中存储String型值.
//     */
//    public void setStringValue(String key, String value) {
//        ValueOperations<String, String> ops = this.stringRedisTemplate.opsForValue();
//        ops.set(key, value);
//    }
//
//    /**
//     * 根据key值从redis中取得String型值.
//     */
//    public String getStringValue(String key) {
//        return this.stringRedisTemplate.opsForValue().get(key);
//    }
//
//    /**
//     * 往redis中存储Object型值.
//     */
//    public void setObjValue(String key, Object objValue) {
//        ValueOperations<String, Object> ops = this.redisTemplate.opsForValue();
//        ops.set(key, objValue);
//    }
//
//    /**
//     * 根据key值从redis中取得Object型值.
//     */
//    public Object getObjValue(String key) {
//        return this.redisTemplate.opsForValue().get(key);
//    }
//
//    /**
//     * 删除指定的key.
//     */
//    public Boolean removeKey(String key) {
//        return this.redisTemplate.delete(key) || this.stringRedisTemplate.delete(key);
//    }
//
//    /**
//     * 指定缓存失效时间
//     *
//     * @param key 键
//     * @param time 时间(秒)
//     * @return
//     */
//    public boolean expire(String key, long time) {
//        try {
//            if (time > 0) {
//                redisTemplate.expire(key, time, TimeUnit.SECONDS);
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 获取所有redis中的key-value
//     *
//     * @return
//     */
//    public HashMap getredis() {
//        Set<String> keys=redisTemplate.keys("*");
//        HashMap<Object,Object> map= new HashMap<>();
//        for (String key:keys){
//            Object value=this.redisTemplate.opsForValue().get(key);
//            map.put(key,value);
//        }
//        return map;
//    }
//}
