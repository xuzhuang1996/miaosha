# miaosha
慕课网秒杀系统。   
ssm入门很好的一个例子：https://www.imooc.com/u/2145618/courses?sort=publish    

- 出现问题1：在写完jsp之后，直接运行tomcat部署时，输入地址栏会报错，显示无法读取c3p0，导致无法生成相应的bean。虽然测试的时候显示连接数据库没问题。   
解决：直接在web-inf下新建lib包，将c3p0跟mchange的jar包复制进去后，添加到buildPath就行了，就是普通添加jar的方式。  
- 出现问题2：路径找了半天没找到。   
解决：一般来说，tomcat下，需要有项目名，如miaosha.有的地方自定义了seckill/，所以需要多加一个seckill/  
- 出现问题3：Could not write content: No serializer found for class dto.SeckillExecution  
解决：dto（就是普通的Java类）数据转json需要dto里类有get\set方法。  


## 流程 ##  

&ensp;&ensp;&ensp;&ensp;第一步，在浏览器输入http://localhost:8888/miaosha/seckill/list 。根据spring-web.xml中的配置，扫描web包下的相关的bean，发现有一个@RequestMapping("/seckill")，表示该url由SeckillController来处理，并且进入SeckillController下的list方法，model将数据绑定，最终返回"list"，根据spring-web.xml中的配置，就是/WEB-INF/jsp/list.jsp。view将根据model中的数据，自动进行动态显示该jsp文件。   
&ensp;&ensp;&ensp;&ensp;第二步，点击Link详情页。比如，点击link的时候url显示为http://localhost:8888/miaosha/seckill/1005/detail ，发现该控制器中有一个@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)的映射方法detail，因此detail方法的seckillId参数将拿到1005这个参数。现在进入detail方法。简单判断：该seckillId是否拿到值，万一中间出错，seckillId没有值，后面就肯定出错。所以判断是否存在seckillId（幸好为Long类型，可以根据null来判断）。接着拿到秒杀对象seckill，{既然是取的数据，用之前都要进行一个判断}，由model绑定后，根据返回的“detail”，进入/WEB-INF/jsp/detail.jsp页面。  
&ensp;&ensp;&ensp;&ensp;第三步，进入detail.jsp页面。刚进入detail.jsp的时候，根据model中的数据，执行seckill.detail.init(seckillId，startTime，endTime)方法进行页面初始化：
- 如果cookie中没有手机号，或者输入的手机号无效。模态窗口将出来。
- 已有手机号也就是登陆成功后。根据后台传入的秒杀对象信息，进行ajax请求获取服务器数据，拿到服务器时间。**在利用Ajax进行web层与service层的数据传输时，建议统一传递数据的类型SeckillResult。在里面设定参数success（是否传输成功），以及泛型data（所传输的数据），以及错误信息error。**
   - nowTime > endTime秒杀结束
   - nowTime < startTime，秒杀未开始,计时事件绑定。待jq的finish.countdown事件开启，进入秒杀处理程序
   - 进入秒杀处理程序。既然可以秒杀了，现在可以请求暴露秒杀地址。Ajax请求"/{seckillId}/exposer"，进入exposer方法。对于该方法的返回值的思考：本来只是需要返回秒杀的地址。但是需要考虑实际业务，如果秒杀开启，需要返回的是秒杀地址，（如生成MD5）但如果秒杀时间未到，返回的是系统时间跟秒杀时间。**已有的实体或者map数据结构无法满足业务需要，因此需要额外定义一个exposer的类，用于返回业务信息。** **提倡复杂逻辑交给service层，control层进行简单逻辑判断，主要判断TRUE FALSE并控制运行分支（得出TRUE FALSE的过程在service层中）**。因此service层用来拿到这个exposer对象，而exposer方法，则选择返回统一传递数据的类型SeckillResult<exposer>的方式，返回数据到web层。也就是handlerSeckill的Ajax拿到数据后的回调函数进行处理：
      - data现在是exposer，其exposed如果为false,说明未开启秒杀，但依然进入了秒杀处理程序。原因：客户端时间跳转不一致，客户端的倒计时结束的时候触发finish.countdown，但不一定就是服务器的时间到了。因此继续countdown方法。
      - exposed如果为true，说明进入正常秒杀运行阶段。点击秒杀按钮，Ajax请求秒杀执行'/miaosha/seckill/seckillId/md5/execution'。进入控制器的execute方法。对该方法的返回值的思考：执行秒杀的时候主要是将seckillId, killPhone, md5这三个参数传进service，核心逻辑会出现几种不同的执行结果，同样，为了方便数据传输，对几种不同结果，可以根据预期的异常，进行封装（同时作数据字典，对异常进行文字解释）。进入控制器的execute方法后，捕捉出现的异常，以SeckillResult<SeckillExecution>的方式返回web层。前台根据返回的数据进行显示执行结果。结束。
