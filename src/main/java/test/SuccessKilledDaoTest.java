package test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import dao.SuccessKilledDao;
import entity.SuccessKilled;


@RunWith(SpringJUnit4ClassRunner.class)//��Ҫ�������SpringJUnit4ClassRunner
@ContextConfiguration("classpath:spring/spring-dao.xml")//����junit spring�������ļ���λ��
public class SuccessKilledDaoTest {

	//ע��Daoʵ��������������@Resource��ʱ�򣬲���spring������DAO���ʵ���࣬ע�뵽�õ�Ԫ������
	//��������0-0-0-0���ԣ���ҪzeroDateTimeBehavior\=convert_To_Null�����
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
