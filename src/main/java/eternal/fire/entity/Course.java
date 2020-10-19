package eternal.fire.entity;

public class Course {
    private int id;
    private String name;
    private String time;
    private String level;
    private String week;
    private String experimenter;

    public Course() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getExperimenter() {
        return experimenter;
    }

    public void setExperimenter(String experimenter) {
        this.experimenter = experimenter;
    }

    public Course(int id, String name, String time, String level, String week, String experimenter) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.level = level;
        this.week = week;
        this.experimenter = experimenter;
    }
}
