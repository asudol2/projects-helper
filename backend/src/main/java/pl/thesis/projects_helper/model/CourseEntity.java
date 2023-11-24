package pl.thesis.projects_helper.model;

import org.apache.commons.lang3.tuple.Pair;

public class CourseEntity {

    private final String courseID;
    private final String termID;
    private final Pair<String, String> names;

    public CourseEntity(String courseID,
                        String termID,
                        String course_name_pl,
                        String course_name_en){
        this.courseID = courseID;
        this.termID = termID;
        this.names = Pair.of(course_name_pl, course_name_en);
    }

    public CourseEntity(String courseID,
                        String termID,
                        Pair<String, String> names){
        this.courseID = courseID;
        this.termID = termID;
        this.names = names;
        }

    public String getCourseID() {
        return courseID;
    }

    public String getTermID() {
        return termID;
    }

    public Pair<String, String> getNames() {
        return names;
    }
}
