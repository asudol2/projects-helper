package pl.thesis.projects_helper.model.response;

import java.util.Iterator;

public record LoginResponse(String id, String firstName, String lastName, String email, String studentNumber) {

    public LoginResponse(Iterable<String> elements) {
        this(
                elements.iterator().next(),
                elements.iterator().next(),
                elements.iterator().next(),
                elements.iterator().next(),
                elements.iterator().next()
        );
    }
}


