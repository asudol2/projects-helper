package pl.thesis.projects_helper.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.*;

public class URLArgsUtilsTest {

    @Test
    public void generateArgsUrlNoArgsTest() {
        Map<String, List<String>> args = new HashMap<>();

        String exp = "";
        assertEquals(exp, URLArgsUtils.generateArgsUrl(args));
    }

    @Test
    public void generateArgsUrlOneArgTest(){
        Map<String, List<String>> args = new HashMap<>();
        args.put("fields", Arrays.asList("first", "second", "third"));

        String exp = "fields=first%7csecond%7cthird";
        assertEquals(exp, URLArgsUtils.generateArgsUrl(args));
    }

    @Test
    public void generateArgsUrlFewArgsWithLastEmptyListTest(){
        Map<String, List<String>> args = new LinkedHashMap<>();
        args.put("fields", Arrays.asList("first", "second", "third"));
        args.put("ores", List.of("gold"));
        args.put("gigs", List.of());

        String exp = "fields=first%7csecond%7cthird&ores=gold";
        assertEquals(exp, URLArgsUtils.generateArgsUrl(args));
    }

    @Test
    public void generateArgsUrlFewArgsWithFirstEmptyListTest(){
        Map<String, List<String>> args = new LinkedHashMap<>();
        args.put("fields", List.of());
        args.put("ores", List.of("gold"));
        args.put("gigs", List.of("first", "second"));

        String exp = "ores=gold&gigs=first%7csecond";
        assertEquals(exp, URLArgsUtils.generateArgsUrl(args));
    }

    @Test
    public void generateArgsUrlFewArgsWithMiddleEmptyListTest(){
        Map<String, List<String>> args = new LinkedHashMap<>();
        args.put("fields", Arrays.asList("first", "second", "third"));
        args.put("ores", List.of());
        args.put("gigs", List.of("gold", "date"));

        String exp = "fields=first%7csecond%7cthird&gigs=gold%7cdate";
        assertEquals(exp, URLArgsUtils.generateArgsUrl(args));
    }
}
