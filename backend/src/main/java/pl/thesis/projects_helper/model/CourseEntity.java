package pl.thesis.projects_helper.model;

import org.apache.commons.lang3.tuple.Pair;

public class CourseEntity {

    private final String courseID;
    private final String termID;
    private final Pair<String, String> names;
    private final String relationshipType;

    public CourseEntity(String courseID) {
        this.courseID = courseID;
        this.termID = null;
        this.names = null;
        this.relationshipType = null;
    }

    public CourseEntity(String courseID,
                        String termID,
                        String courseNamePl,
                        String courseNameEn,
                        String relationshipType){
        this.courseID = courseID;
        this.termID = termID;
        this.names = Pair.of(courseNamePl, courseNameEn);
        this.relationshipType = relationshipType;
    }

    public CourseEntity(String courseID,
                        String termID,
                        Pair<String, String> names,
                        String relationshipType){
        this.courseID = courseID;
        this.termID = termID;
        this.names = names;
        this.relationshipType = relationshipType;
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

    public String getRelationshipType() {
        return relationshipType;
    }
}
