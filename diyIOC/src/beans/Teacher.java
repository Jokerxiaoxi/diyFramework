package beans;

/**
 * @description Teacher bean
 */
public class Teacher {
    private String name;    // 老师姓名
    private String cls;     // 所在班级

    public Teacher() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", cls='" + cls + '\'' +
                '}';
    }
}
