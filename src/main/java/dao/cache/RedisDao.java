package dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private static final Logger LOG = LoggerFactory.getLogger(RedisDao.class);
	private final JedisPool jedisPool;
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);//protobuff需要自己写schema描述。protostuff提供了这个可以动态描述
	
	public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }
	
	//基本操作get:byte[]->反序列化->Object(Seckill)
	public Seckill getSeckill(long seckillId) {
		try {
            Jedis jedis = jedisPool.getResource();
            try {
            	//key的值是"seckill:1001"，如果不加，可能别的进来也是1001，这个value就被覆盖了。因此这是规范化标识。
                String key = "seckill:" + seckillId;
                // 并没有实现内部序列化操作.而Java自带的序列化接口性能不好，选择谷歌protostuff
                // 原理：存取的时候是通过序列化工具类把对象序列化成一个字节数组，再把这个数据存到redis中。
                // 告诉我对象的class,内部有schema来描述你的class是什么结构，class必须有get/set方法这种标准的类（也就是pojo），而不是string等类
                // 采用自定义序列化

                byte[] bytes = jedis.get(key.getBytes());//拿到这个key对应的value。
                if (bytes != null) {
                    //空对象
                    Seckill seckill = schema.newMessage();//提供一个空对象，传进下面的函数
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);//将bytes合并到seckill这个空对象下。
                    //seckill 被反序列化，也就是拿到值了。
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
		return null;
	}
	
	//set:Object(Seckill)->序列化->byte[] ->发送给redis
	public String setSeckill(Seckill seckill) {
		try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                //最后需要一个缓存器。当对象特别大的时候会有一个缓冲。
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存，虽然有set
                int timeOut = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeOut, bytes);//返回值Status code reply
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
		return null;
	}
}
