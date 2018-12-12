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
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);//protobuff��Ҫ�Լ�дschema������protostuff�ṩ��������Զ�̬����
	
	public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }
	
	//��������get:byte[]->�����л�->Object(Seckill)
	public Seckill getSeckill(long seckillId) {
		try {
            Jedis jedis = jedisPool.getResource();
            try {
            	//key��ֵ��"seckill:1001"��������ӣ����ܱ�Ľ���Ҳ��1001�����value�ͱ������ˡ�������ǹ淶����ʶ��
                String key = "seckill:" + seckillId;
                // ��û��ʵ���ڲ����л�����.��Java�Դ������л��ӿ����ܲ��ã�ѡ��ȸ�protostuff
                // ԭ����ȡ��ʱ����ͨ�����л�������Ѷ������л���һ���ֽ����飬�ٰ�������ݴ浽redis�С�
                // �����Ҷ����class,�ڲ���schema���������class��ʲô�ṹ��class������get/set�������ֱ�׼���ࣨҲ����pojo����������string����
                // �����Զ������л�

                byte[] bytes = jedis.get(key.getBytes());//�õ����key��Ӧ��value��
                if (bytes != null) {
                    //�ն���
                    Seckill seckill = schema.newMessage();//�ṩһ���ն��󣬴�������ĺ���
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);//��bytes�ϲ���seckill����ն����¡�
                    //seckill �������л���Ҳ�����õ�ֵ�ˡ�
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
	
	//set:Object(Seckill)->���л�->byte[] ->���͸�redis
	public String setSeckill(Seckill seckill) {
		try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                //�����Ҫһ�����������������ر���ʱ�����һ�����塣
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //��ʱ���棬��Ȼ��set
                int timeOut = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeOut, bytes);//����ֵStatus code reply
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
