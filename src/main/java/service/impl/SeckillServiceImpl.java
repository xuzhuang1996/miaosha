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
	//md5盐值字符串,用于混淆md5,随便写
    private final String slat = "asdfasd2341242@#$@#$%$%%#@$%#@%^%^";
	
    //注入service依赖，常见的还有resource等注解
    @Autowired
	private SeckillDao seckillDao;
    @Autowired
	private SuccessKilledDao successKilledDao;
    @Autowired
	private RedisDao redisDao;
	
	public List<Seckill> getSeckillList() {
		// TODO Auto-generated method stub
		return seckillDao.queryAll(0, 1000);//就是tmall的分页查询
	}

	public Seckill getById(Long seckillId) {
		// TODO Auto-generated method stub
		return seckillDao.queryById(seckillId);
	}

	/*
	 *  如果seckillId为空或者系统当前时间早于秒杀时间或者当前系统时间晚于秒杀时间，都不开启秒杀，而是根据业务需求返回相应的数据信息，以方便客户查看信息
	 *  其他情况，可以开启秒杀，但在开启秒杀之前，需要给用户注册一个MD5串，同时提供秒杀信息，（MD5用于接下来秒杀时身份验证） 
	 */
	public Exposer exportSeckillUrl(Long seckillId) {
		//缓存优化
		//1.访问redis
		//Seckill seckill = seckillDao.queryById(seckillId);//对这行进行优化
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
            //2.访问数据库
            seckill = getById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3.放入redis
                redisDao.setSeckill(seckill);
            }
        }
		
//		//秒杀产品都没有了.这里就多余了
//		if(seckill == null)
//			return new Exposer(false, seckillId);
		//秒杀产品还在的情况下
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
      *   使用注解控制事务的优点:
     * 1.开发团队达成一致约定,明确标注事务方法的编程风格.
     * 2.保证事务方法的执行时间尽可能短,不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部.
     * 3.不是所有的方法都需要事务.如一些查询的service.只有一条修改操作的service.
     */
	@Transactional
	public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		/*
		 * 1000元秒杀iphone6这个name下的id
		 * 根据执行秒杀时的md5跟id,确定，如果改了md5或者id，匹配不上。
		 * 用户是想在暴露秒杀接口之后，利用一些插件，反复秒杀不同的商品。
		 * 如果加入了md5，在发送回后台的时候由于seckillId不一样，则md5就不一样，这样就能防止上述情况的发生
		 */
		if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException(SeckillStatEnum.DATA_REWRITE.getStateInfo());
        }
		//本来执行秒杀逻辑:1.减库存.2.记录购买行为
		// mysql的行级锁是针对索引，InnerDB支持行锁也支持表锁。只有通过索引条件检索数据，InnoDB才使用行级锁，否则，InnoDB将使用表锁！
		// 行级锁的缺点是：由于需要请求大量的锁资源，所以速度慢，内存消耗大，并且可能导致大量的锁冲突，从而影响并发性能。
		// insertSuccessKilled这个操作没有行级索。而reduceNumber的操作，为updata操作具有行级索。
		// 为了优化：如果更新操作在前，那么就需要执行完更新和插入以后事务提交或回滚才释放锁，如果插入在前，则更新完以后事务提交或回滚就释放锁。
		// 结论：更新在前，加锁和释放锁之间两次的网络延迟和GC，如果插入在前，则加锁和释放锁之间只有一次的网络延迟和GC，也就是减少的持有锁的时间。
		// 结论：update同一行会导致行级锁，而insert是可以并行执行的，线程可以并发insert。因此时间减少了。
		Date nowTime = new Date();
		try {

            //记录购买行为
            int inserCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (inserCount <= 0) {
                //重复秒杀
                throw new RepeatKillException(SeckillStatEnum.REPEAT_KILL.getStateInfo());
            } else {
                //减库存 
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);

                if (updateCount <= 0) {
                    //rollback
                    throw new SeckillCloseException(SeckillStatEnum.END.getStateInfo());
                } else {
                    //秒杀成功  commit
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
            //所有的编译期异常转化为运行期异常,spring的声明式事务做rollback
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
