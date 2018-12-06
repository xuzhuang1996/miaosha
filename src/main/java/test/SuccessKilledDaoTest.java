package test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import dao.SuccessKilledDao;
import entity.SuccessKilled;


@RunWith(SpringJUnit4ClassRunner.class)//需要加载这个SpringJUnit4ClassRunner
@ContextConfiguration("classpath:spring/spring-dao.xml")//告诉junit spring的配置文件的位置
public class SuccessKilledDaoTest {

	//注入Dao实现类依赖。看到@Resource的时候，查找spring容器的DAO这个实现类，注入到该单元测试类
	//日期数据0-0-0-0不对，需要zeroDateTimeBehavior\=convert_To_Null来解决
	@Resource
    private SuccessKilledDao successKilledDao;
	@Test
	public void testInsertSuccessKilled() {
		long id = 1000L;
        long phone = 15811112222L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount: " + insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id = 1000L;
        long phone = 15811112222L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        if (successKilled != null) {
            System.out.println(successKilled.getSeckill());
        }
	}

}
