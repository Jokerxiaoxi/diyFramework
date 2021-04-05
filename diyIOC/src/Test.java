import beans.Student;
import beans.Teacher;
import ioc.DiyIOC;

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
