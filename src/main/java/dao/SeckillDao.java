package dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.Seckill;

//mybatis�Զ�������ʵ��DAO����ࡣ��spring��bean��ע��û��DAO������ֻ࣬�нӿڡ����ϵ�ʱ������Զ�ע��spring����������дbean������
public interface SeckillDao {
	/**
     * �����
     *
     * @param seckillId
     * @param killTime
     * @return�����������������1,��ʾ���µ�����
     */
    Integer reduceNumber(@Param("seckillId") Long seckillId, @Param("killTime") Date killTime);

    /**
     * ����ID��ѯ��ɱ����
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(Long seckillId);


    /**
     * ����ƫ������ѯ��ɱ��Ʒ�б�
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * ʹ�ô洢����ִ����ɱ
     *
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);
}
