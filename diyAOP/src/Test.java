/**
 * @author 章霆
 * @date 2021/3/15 17:14
 * @description 测试类
 */
public class Test {
    public static void main(String[] args) {
        // 1. 创建一个 MethodInvocation 实现类
        MethodInvocation before = () -> System.out.println("去上学！！");
        MethodInvocation after = () -> System.out.println("放学了！！");
        StuService stuService = new StuServiceImpl();

        // 2. 创建一个前置通知和一个后置通知
        Advice beforeAdvice = new BeforeAdvice(stuService, before);
        Advice afterAdvice = new AfterAdvice(stuService, after);

        // 3. 为目标对象生成代理
        StuService helloServiceImplProxy = (StuService) DiyAOP.getProxy(stuService, beforeAdvice);
        StuService helloServiceImplProxy1 = (StuService) DiyAOP.getProxy(stuService, afterAdvice);

        helloServiceImplProxy.study();
        System.out.println("===========================");
        helloServiceImplProxy1.study();
    }
}
