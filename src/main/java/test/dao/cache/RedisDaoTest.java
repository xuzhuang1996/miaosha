package test.dao.cache;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dao.SeckillDao;
import dao.cache.RedisDao;
import entity.Seckill;

@RunWith(SpringJUnit4ClassRunner.class)//��Ҫ�������SpringJUnit4ClassRunner
@ContextConfiguration("classpath:spring/spring-dao.xml")//����junit spring�������ļ���λ��
public class RedisDaoTest {

	private long id = 1001;
	@Autowired
	private RedisDao redisDao;
	@Autowired
	private SeckillDao seckillDao;//ͨ��DAO�õ��������
	
	@Test
	public void testGetSeckill() {
		Seckill seckill = redisDao.getSeckill(id);
        if (seckill == null) {
            seckill = seckillDao.queryById(id);
            if (seckill != null) {
                String result = redisDao.setSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
	}

}
