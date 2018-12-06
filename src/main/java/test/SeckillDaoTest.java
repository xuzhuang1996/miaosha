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
 * ����Spring��Junit����,junit����ʱ����springIOC����
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)//��Ҫ�������SpringJUnit4ClassRunner
@ContextConfiguration("classpath:spring/spring-dao.xml")//����junit spring�������ļ���λ��
public class SeckillDaoTest {

	//ע��Daoʵ��������������@Resource��ʱ�򣬲���spring������DAO���ʵ���࣬ע�뵽�õ�Ԫ������
    @Resource
    private SeckillDao seckillDao;
    
	@Test
	public void testReduceNumber() {
//      Javaû�б����βεļ�¼:QueryAll(int offset,int limit)->QueryAll(arg0,arg1);
//      ��Ϊjava�βε�����,����������Ͳ�����ʱ����Ҫ��@Param("seckillId")ע�����ֿ���
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount:  " + updateCount);
	}

	@Test
	public void testQueryById() {
		long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
        //���Ե�ʱ��û�ҵ�Seckill���ʵ���࣬����SqlSessionFactoryʱ�������⣬����������ļ�springDAO.xml�����ʵ��������á�
        //Seckill [seckillId=1000, name=1000Ԫ��ɱiphone6, number=100, startTime=Fri Jan 01 00:00:00 CST 2016, endTime=Sat Jan 02 00:00:00 CST 2016, createTime=Wed Dec 05 16:30:48 CST 2018]
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
