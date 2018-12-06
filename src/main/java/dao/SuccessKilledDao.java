package dao;

import org.apache.ibatis.annotations.Param;

import entity.SuccessKilled;

public interface SuccessKilledDao {
	/**
     * ���빺����ϸ,�ɹ����ظ�(���ݿ�����������)
     *
     * @param seckilledId
     * @param userPhone
     * @return
     */
    Integer insertSuccessKilled(@Param("seckilledId") long seckilledId, @Param("userPhone") long userPhone);

    /**
     * ����ID��ѯSuccessKilled��Я����ɱ��Ʒ����ʵ��
     *
     * @param seckilledId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckilledId") long seckilledId, @Param("userPhone") long userPhone);
}
