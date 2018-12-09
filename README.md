# miaosha
慕课网秒杀系统。   
ssm入门很好的一个例子：https://www.imooc.com/u/2145618/courses?sort=publish    
出现问题1：在写完jsp之后，直接运行tomcat部署时，输入地址栏会报错，显示无法读取c3p0，导致无法生成相应的bean。虽然测试的时候显示连接数据库没问题。   
解决：直接在web-inf下新建lib包，将c3p0跟mchange的jar包复制进去后，添加到buildPath就行了，就是普通添加jar的方式。  
出现问题2：路径找了半天没找到。   
解决：一般来说，tomcat下，需要有项目名，如miaosha.有的地方自定义了seckill/，所以需要多加一个seckill/
