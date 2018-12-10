package web;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dto.Exposer;
import dto.SeckillExecution;
import dto.SeckillResult;
import entity.Seckill;
import enums.SeckillStatEnum;
import exception.RepeatKillException;
import exception.SeckillCloseException;
import service.SeckillService;

//提倡复杂逻辑交给service层，control层进行简单逻辑判断，主要判断TRUE FALSE并控制运行分支（得出TRUE FALSE的过程在service层中）

@Controller//将这个类放入容器
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分
public class SeckillController {
	private static final Logger LOG = LoggerFactory.getLogger(SeckillController.class);
	//一个类，俩个实现类，Autowired就不知道注入哪一个实现类，而Resource有name属性，可以区分
	@Autowired
    private SeckillService seckillService;
	
	//执行这个方法，肯定是需要掉service方法。因此将service对象注入
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //list.jsp + model = ModelAndView
        //获取列表页

        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);

        return "list";//根据springweb中的配置，就是/WEB-INF/jsp/list.jsp
    }
	
	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		if (seckillId == null) {
            return "redirect:/seckill/list";
        }
		Seckill seckill = seckillService.getById(seckillId);

        if (seckill == null) {
            return "forward:/seckill/list";//这里都可以
        }
        model.addAttribute("seckill", seckill);
		return "detail";
	}
	
    //  ajax json。前端的请求该url,返回的是SeckillResult<Exposer>
	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result = null;
		try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
		return result;
	}
	
	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false) Long killPhone){
		if (killPhone == null) {
            return new SeckillResult<SeckillExecution>(false, SeckillStatEnum.NOT_LOGIN.getStateInfo());
        }

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, killPhone, md5);
            //SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, killPhone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);

        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
	}
	
	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true, now.getTime());
    }
	
	
	
	
	
}
