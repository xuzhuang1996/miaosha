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
 * 业务接口:站在"使用者"的角度设计接口
 * 1.方法的定义的粒度.2.参数.3.返回类型(return /异常)（如果一般返回类型不够用，自己封装一个返回类）
 */
public interface SeckillService {

	/**
     * 查询所有秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();
    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(Long seckillId);
    
    /**
     * 秒杀开启前谁也拿不到这个地址，就是谁也进不去
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(Long seckillId);
    
    /**
     * 执行秒杀操作
     *这么写主要是告诉后来者，可能有这几种异常
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5) throws SeckillException
            , RepeatKillException, SeckillCloseException;
}
