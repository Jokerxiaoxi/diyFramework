import java.lang.reflect.Method;

/**
 * @description 实现了Advice接口，是一个后置通知
 */
public class AfterAdvice implements Advice {
    private Object bean;
    private MethodInvocation methodInvocation;

    public AfterAdvice(Object bean, MethodInvocation methodInvocation) {
        this.bean = bean;
        this.methodInvocation = methodInvocation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = method.invoke(bean, args);
        // 在目标方法执行前调用通知
        methodInvocation.invoke();
        return invoke;
    }
}
