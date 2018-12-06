package test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dao.SeckillDao;
import entity.Seckill;

import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 配置Spring和Junit整合,junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)//需要加载这个SpringJUnit4ClassRunner
@ContextConfiguration("classpath:spring/spring-dao.xml")//告诉junit spring的配置文件的位置
public class SeckillDaoTest {

	//注入Dao实现类依赖。看到@Resource的时候，查找spring容器的DAO这个实现类，注入到该单元测试类
    @Resource
    private SeckillDao seckillDao;
    
	@Test
	public void testReduceNumber() {
//      Java没有保存形参的记录:QueryAll(int offset,int limit)->QueryAll(arg0,arg1);
//      因为java形参的问题,多个基本类型参数的时候需要用@Param("seckillId")注解区分开来
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount:  " + updateCount);
	}

	@Test
	public void testQueryById() {
		long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
        //测试的时候没找到Seckill这个实体类，配置SqlSessionFactory时出的问题，因此找配置文件springDAO.xml里面的实体类的配置。
        //Seckill [seckillId=1000, name=1000元秒杀iphone6, number=100, startTime=Fri Jan 01 00:00:00 CST 2016, endTime=Sat Jan 02 00:00:00 CST 2016, createTime=Wed Dec 05 16:30:48 CST 2018]
	}

	@Test
	public void testQueryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
	}

	@Test
	public void testKillByProcedure() {
		fail("Not yet implemented");
	}

}
