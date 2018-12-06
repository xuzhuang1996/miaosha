package dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.Seckill;

//mybatis自动帮我们实现DAO这个类。而spring中bean的注入没有DAO具体的类，只有接口。整合的时候可以自动注入spring容器。不用写bean的配置
public interface SeckillDao {
	/**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return　如果更新行数大于1,表示更新的行数
     */
    Integer reduceNumber(@Param("seckillId") Long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据ID查询秒杀对象
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(Long seckillId);


    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 使用存储过程执行秒杀
     *
     * @param paramMap
     */
    void killByProcedure(Map<String, Object> paramMap);
}
