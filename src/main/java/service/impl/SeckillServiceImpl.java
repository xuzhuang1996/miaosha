package service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import dao.SeckillDao;
import dao.SuccessKilledDao;
import dao.cache.RedisDao;
import dto.Exposer;
import dto.SeckillExecution;
import entity.Seckill;
import entity.SuccessKilled;
import enums.SeckillStatEnum;
import exception.RepeatKillException;
import exception.SeckillCloseException;
import exception.SeckillException;
import service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService{

	private static final Logger LOG = LoggerFactory.getLogger(SeckillServiceImpl.class);
	//md5��ֵ�ַ���,���ڻ���md5,���д
    private final String slat = "asdfasd2341242@#$@#$%$%%#@$%#@%^%^";
	
    //ע��service�����������Ļ���resource��ע��
    @Autowired
	private SeckillDao seckillDao;
    @Autowired
	private SuccessKilledDao successKilledDao;
    @Autowired
	private RedisDao redisDao;
	
	public List<Seckill> getSeckillList() {
		// TODO Auto-generated method stub
		return seckillDao.queryAll(0, 1000);//����tmall�ķ�ҳ��ѯ
	}

	public Seckill getById(Long seckillId) {
		// TODO Auto-generated method stub
		return seckillDao.queryById(seckillId);
	}

	/*
	 *  ���seckillIdΪ�ջ���ϵͳ��ǰʱ��������ɱʱ����ߵ�ǰϵͳʱ��������ɱʱ�䣬����������ɱ�����Ǹ���ҵ�����󷵻���Ӧ��������Ϣ���Է���ͻ��鿴��Ϣ
	 *  ������������Կ�����ɱ�����ڿ�����ɱ֮ǰ����Ҫ���û�ע��һ��MD5����ͬʱ�ṩ��ɱ��Ϣ����MD5���ڽ�������ɱʱ�����֤�� 
	 */
	public Exposer exportSeckillUrl(Long seckillId) {
		//�����Ż�
		//1.����redis
		//Seckill seckill = seckillDao.queryById(seckillId);//�����н����Ż�
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
            //2.�������ݿ�
            seckill = getById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3.����redis
                redisDao.setSeckill(seckill);
            }
        }
		
//		//��ɱ��Ʒ��û����.����Ͷ�����
//		if(seckill == null)
//			return new Exposer(false, seckillId);
		//��ɱ��Ʒ���ڵ������
		Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        
        if (nowTime.getTime() > endTime.getTime() || nowTime.getTime() < startTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        
        String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

    /**
      *   ʹ��ע�����������ŵ�:
     * 1.�����ŶӴ��һ��Լ��,��ȷ��ע���񷽷��ı�̷��.
     * 2.��֤���񷽷���ִ��ʱ�価���ܶ�,��Ҫ���������������RPC/HTTP������߰��뵽���񷽷��ⲿ.
     * 3.�������еķ�������Ҫ����.��һЩ��ѯ��service.ֻ��һ���޸Ĳ�����service.
     */
	@Transactional
	public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		/*
		 * 1000Ԫ��ɱiphone6���name�µ�id
		 * ����ִ����ɱʱ��md5��id,ȷ�����������md5����id��ƥ�䲻�ϡ�
		 * �û������ڱ�¶��ɱ�ӿ�֮������һЩ�����������ɱ��ͬ����Ʒ��
		 * ���������md5���ڷ��ͻغ�̨��ʱ������seckillId��һ������md5�Ͳ�һ�����������ܷ�ֹ��������ķ���
		 */
		if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException(SeckillStatEnum.DATA_REWRITE.getStateInfo());
        }
		//����ִ����ɱ�߼�:1.�����.2.��¼������Ϊ
		// mysql���м��������������InnerDB֧������Ҳ֧�ֱ�����ֻ��ͨ�����������������ݣ�InnoDB��ʹ���м���������InnoDB��ʹ�ñ�����
		// �м�����ȱ���ǣ�������Ҫ�������������Դ�������ٶ������ڴ����Ĵ󣬲��ҿ��ܵ��´���������ͻ���Ӷ�Ӱ�첢�����ܡ�
		// insertSuccessKilled�������û���м�������reduceNumber�Ĳ�����Ϊupdata���������м�����
		// Ϊ���Ż���������²�����ǰ����ô����Ҫִ������ºͲ����Ժ������ύ��ع����ͷ��������������ǰ����������Ժ������ύ��ع����ͷ�����
		// ���ۣ�������ǰ���������ͷ���֮�����ε������ӳٺ�GC�����������ǰ����������ͷ���֮��ֻ��һ�ε������ӳٺ�GC��Ҳ���Ǽ��ٵĳ�������ʱ�䡣
		// ���ۣ�updateͬһ�лᵼ���м�������insert�ǿ��Բ���ִ�еģ��߳̿��Բ���insert�����ʱ������ˡ�
		Date nowTime = new Date();
		try {

            //��¼������Ϊ
            int inserCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (inserCount <= 0) {
                //�ظ���ɱ
                throw new RepeatKillException(SeckillStatEnum.REPEAT_KILL.getStateInfo());
            } else {
                //����� 
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);

                if (updateCount <= 0) {
                    //rollback
                    throw new SeckillCloseException(SeckillStatEnum.END.getStateInfo());
                } else {
                    //��ɱ�ɹ�  commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            //���еı������쳣ת��Ϊ�������쳣,spring������ʽ������rollback
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
	}
	
	private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        LOG.info("_________________________________md5: " + md5);
        return md5;
    }

}
