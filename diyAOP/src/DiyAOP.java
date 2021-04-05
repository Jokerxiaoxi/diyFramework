import java.lang.reflect.Proxy;

/**
 * @description 生成代理类
 */
public class DiyAOP {
    public static Object getProxy(Object bean, Advice advice) {
        return Proxy.newProxyInstance(DiyAOP.class.getClassLoader(),
                bean.getClass().getInterfaces(), advice);
    }
}
