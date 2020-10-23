package eternal.fire.entity;

public class Student extends User {
    private String id;
    private String _class;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Student student = (Student) obj;
        return this.name.equals(student.name);
    }

    public Student(String id, String name, String email, String _class, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this._class = _class;
        this.password = password;
    }

    public Student() {
        this.imagUrl = "http://ww1.sinaimg.cn/large/005VT09Qly1gitqwga9f7j30g40g4jru.jpg";
    }
}
