package beans;

/**
 * @description Student bean
 */
public class Student {
    private String name;        // 姓名
    private String age;         // 年龄
    private String num;         // 学号
    private Teacher teacher;    // 所属老师

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", num='" + num + '\'' +
                ", teacher=" + teacher +
                '}';
    }
}
