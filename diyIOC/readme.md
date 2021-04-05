## 前提：Spring官方是如何实现IOC容器的？

想要自己手动实现Spring IOC，就得先知道Spring官方是怎么实现的，让我们看一下吧！！

```java
public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
}
```

以上代码就可以**利用配置文件来启动一个Spring容器**了，使用前提是加上相关maven或gradle依赖，这里就不分析了

首先，定义一个接口：

```java
public interface MessageService {
    String getMessage();
}
```

定义接口实现类：

```java
public class MessageServiceImpl implements MessageService {
    public String getMessage() {
        return "hello world";
    }
}
```

接着，创建 `application.xml` 文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd" default-autowire="byName">

    <bean id="messageService" class="test.MessageServiceImpl"/>
</beans>
```

简单使用

```java
public class Test {
    public static void main(String[] args) {
        // 用我们的配置文件来启动一个ApplicationContext
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");
        // 通过IOC容器取出我们的Bean，而不是用new MessageServiceImpl()这种方式
        MessageService messageService = context.getBean(MessageService.class);
        // 输出：hello world
        System.out.println(messageService.getMessage());
    }
}
```

## 动手实现IOC容器

这里我们实现的是常见的单例bean

### 1. 步骤

1. 加载 xml 配置文件，遍历其中的标签
2. 获取标签中的 id 和 class 属性，加载 class 属性对应的类，并创建 bean
3. 遍历标签中的标签，获取属性值，并将属性值填充到 bean 中
4. 将 bean 注册到 bean 容器中

### 2. 代码结构分析

DiyIOC     			      // IOC的实现类，实现了上面所说的4个步骤
Student           		 // bean
Teacher        			// 同上 
ioc.xml       			   // bean 配置文件
Test				      	 // IOC的测试类

**DiyIOC.java**

```java
/**
 * @author: 章霆
 * @date: 2021/3/15 15:32
 * @description 读取xml文件，遍历读取属性值，通过反射生成并注入属性，并放到IOC容器中（map）
 */

public class DiyIOC {
    // ioc容器
    private Map<String, Object> beanMap = new HashMap<>();

    // 初始化
    public DiyIOC(String location) throws Exception {
        loadBeans(location);
    }

    // 获取bean
    public Object getBean(String name) {
        Object bean = beanMap.get(name);
        if (bean == null) {
            throw new IllegalArgumentException("there is no bean with name " + name);
        }
        return bean;
    }

    private void loadBeans(String location) throws Exception {
        // 加载 xml 配置文件
        InputStream inputStream = new FileInputStream(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();

        // 遍历 <bean> 标签
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                String id = ele.getAttribute("id");
                String className = ele.getAttribute("class");

                // 加载 beanClass
                Class beanClass = null;
                try {
                    beanClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                // 创建 bean
                Object bean = beanClass.newInstance();

                // 遍历 <property> 标签
                NodeList propertyNodes = ele.getElementsByTagName("property");
                for (int j = 0; j < propertyNodes.getLength(); j++) {
                    Node propertyNode = propertyNodes.item(j);
                    if (propertyNode instanceof Element) {
                        Element propertyElement = (Element) propertyNode;
                        String name = propertyElement.getAttribute("name");
                        String value = propertyElement.getAttribute("value");

                        // 利用反射将 bean 相关字段访问权限设为可访问
                        Field declaredField = bean.getClass().getDeclaredField(name);
                        declaredField.setAccessible(true);

                        if (value != null && value.length() > 0) {
                            // 将属性值填充到相关字段中
                            declaredField.set(bean, value);
                        } else {
                            String ref = propertyElement.getAttribute("ref");
                            if (ref == null || ref.length() == 0) {
                                throw new IllegalArgumentException("ref config error");
                            }
                            // 将引用填充到相关字段中
                            declaredField.set(bean, getBean(ref));
                        }
                        // 将 bean 注册到 bean 容器中
                        registerBean(id, bean);
                    }
                }
            }
        }
    }

    // 注册bean
    private void registerBean(String id, Object bean) {
        beanMap.put(id, bean);
    }
}
```

**Student.java**

```java
/**
 * @description Student bean
 */
public class Student {
    private String name;        // 姓名
    private String age;         // 年龄
    private String num;         // 学号
    private Teacher teacher;    // 所属老师
    ... 						// 省略。包括默认构造，set、get方法以及toString方法
}
```

**Teacher.java**

```java
/**
 * @description Teacher bean
 */
public class Teacher {
    private String name;    // 老师姓名
    private String cls;     // 所在班级
    ...						// 省略
}
```

**ioc.xml**

```xml
<beans>
    <bean id="teacher" class="beans.Teacher">
        <property name="name" value="王涛" />
        <property name="cls" value="软工3班" />
    </bean>

    <bean id="student" class="beans.Student">
        <property name="name" value="章霆" />
        <property name="age" value="21" />
        <property name="num" value="20181004093" />
        <property name="teacher" ref="teacher" />
    </bean>
</beans>
```

**Test.java**

```java
public class Test {

    private static String location;
    private static DiyIOC diyIOC;

    // 初始化ioc容器
    static {
        try {
            location = DiyIOC.class.getClassLoader().getResource("xml/ioc.xml").getFile();
            diyIOC = new DiyIOC(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 测试使用
        Teacher teacher = (Teacher) diyIOC.getBean("teacher");
        Student student = (Student) diyIOC.getBean("student");
        System.out.println(teacher);
        System.out.println(student);
    }
}
```

启动Test.java类，测试结果如下：

![diyIOC](https://gitee.com/Jokerxiaoxi996/images/blob/master/diyIOC.png)

成功实现，芜湖~
