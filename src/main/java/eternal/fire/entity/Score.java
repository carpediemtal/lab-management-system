package eternal.fire.entity;

public class Score {
    private String courseName;
    private int score;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Score() {

    }

    public Score(String courseName, int score) {
        this.courseName = courseName;
        this.score = score;
    }
}
