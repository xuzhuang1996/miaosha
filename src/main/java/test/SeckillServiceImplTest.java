package test;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dto.Exposer;
import dto.SeckillExecution;
import entity.Seckill;
import service.SeckillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})//代表启动时容器加载哪些配置
public class SeckillServiceImplTest {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	@Autowired
    private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() {
		List<Seckill>list = seckillService.getSeckillList();
		LOG.info("list="+list);
	}

	@Test
	public void testGetById() {
		long id = 1000;
        Seckill seckill = seckillService.getById(id);
        LOG.info("seckill="+seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long id = 1004;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        LOG.info("exposer="+exposer);
        //exposer=Exposer [exposed=true, md5=e166d026254be6f288c7de9e55fc1356, seckillId=1004, now=0, start=0, end=0]
	}

	@Test
	public void testExecuteSeckill() {
		long seckillId = 1004;
        long userPhone = 15821739111L;
        String md5 = "e166d026254be6f288c7de9e55fc1356";
        try {
        SeckillExecution s = seckillService.executeSeckill(seckillId, userPhone, md5);
        LOG.info("SeckillExecution="+s);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
	}

}
