总所周知，Spring AOP是通过动态代理来实现的，包括jdk动态代理（基于接口）和cglib动态代理（基于子类），这里我采用基于jdk的动态代理来实现AOP

```
Spring 中对应了 5 种不同类型的通知：
· 前置通知（Before）：在目标方法执行前，执行通知
· 后置通知（After）：在目标方法执行后，执行通知，此时不关系目标方法返回的结果是什么
· 返回通知（After-returning）：在目标方法执行后，执行通知
· 异常通知（After-throwing）：在目标方法抛出异常后执行通知
· 环绕通知（Around）: 目标方法被通知包裹，通知在目标方法执行前和执行后都被会调用
```

这里我测试实现**前置通知**和**后置通知**，涉及到的类如下：

```
MethodInvocation 接口  // 实现类包含了切面逻辑
Advice 接口        	 // 继承了InvocationHandler接口
BeforeAdvice 类    	  // 实现了Advice接口，是一个前置通知
AfterAdvice 类    	  // 实现了Advice接口，是一个后置通知
DiyAOP 类       		  // 生成代理类
Test类      			// 测试类
StuService接口   	 // 目标对象接口
StuServiceImpl   	   // 目标对象
```

**MethodInvocation接口**

```java
/**
 * @description 实现类包含了切面逻辑
 */
public interface MethodInvocation {
    void invoke();
}
```

**Advice接口**

```java
/**
 * @description 继承了InvocationHandler接口
 */
public interface Advice extends InvocationHandler {
}
```

**BeforeAdvice类**

```java
/**
 * @description 实现了Advice接口，是一个前置通知
 */
public class BeforeAdvice implements Advice {
    private Object bean;
    private MethodInvocation methodInvocation;

    public BeforeAdvice(Object bean, MethodInvocation methodInvocation) {
        this.bean = bean;
        this.methodInvocation = methodInvocation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在目标方法执行前调用通知
        methodInvocation.invoke();
        return method.invoke(bean, args);
    }
}
```

**AfterAdvice类**

```java
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
```

**DiyAOP类** 

```java
/**
 * @description 生成代理类
 */
public class DiyAOP {
    public static Object getProxy(Object bean, Advice advice) {
        return Proxy.newProxyInstance(DiyAOP.class.getClassLoader(),
                bean.getClass().getInterfaces(), advice);
    }
}
```

**StuService接口**

```java
/**
 * 目标对象接口
 */
public interface StuService {
    void study();
}
```

**StuServiceImpl实现类**

```java
/**
 * @description 目标对象
 */
public class StuServiceImpl implements StuService {
    @Override
    public void study() {
        System.out.println("学习！！");
    }
}
```

**Test类**

```java
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
```

执行Test类，执行结果如下：

![5252](C:\Users\Joker\Desktop\5252.png)

