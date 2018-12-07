package service;

import java.util.List;

import dto.Exposer;
import dto.SeckillExecution;
import entity.Seckill;
import exception.RepeatKillException;
import exception.SeckillCloseException;
import exception.SeckillException;

/**
 * 
 * ҵ��ӿ�:վ��"ʹ����"�ĽǶ���ƽӿ�
 * 1.�����Ķ��������.2.����.3.��������(return /�쳣)�����һ�㷵�����Ͳ����ã��Լ���װһ�������ࣩ
 */
public interface SeckillService {

	/**
     * ��ѯ������ɱ��¼
     *
     * @return
     */
    List<Seckill> getSeckillList();
    /**
     * ��ѯ������ɱ��¼
     *
     * @param seckillId
     * @return
     */
    Seckill getById(Long seckillId);
    
    /**
     * ��ɱ����ǰ˭Ҳ�ò��������ַ������˭Ҳ����ȥ
     * ��ɱ����ʱ�����ɱ�ӿڵ�ַ
     * �������ϵͳʱ�����ɱʱ��
     *
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(Long seckillId);
    
    /**
     * ִ����ɱ����
     *��ôд��Ҫ�Ǹ��ߺ����ߣ��������⼸���쳣
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5) throws SeckillException
            , RepeatKillException, SeckillCloseException;
}
