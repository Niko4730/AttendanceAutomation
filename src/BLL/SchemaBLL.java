package BLL;

import BE.Account;
import BE.Course;
import BE.Lesson;
import BE.Schema;
import DAL.SchemaDAL;
import UTIL.ArrayTools;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class SchemaBLL {
    private SchemaDAL dbAccess;

    private List<Lesson> allLessons;

    private Account account;

    public SchemaBLL(Account a) throws SQLException {
        account = a;
        dbAccess = new SchemaDAL();
        allLessons = new ArrayList<>();

        allLessons.addAll(dbAccess.getAllLessons(a));
    }

    public List<Lesson> getAllLessons() {
        return allLessons;
    }

    public List<List<Lesson>> getWeekSchema() {
        Calendar cal = Calendar.getInstance();

        List<Lesson> monday = new ArrayList<>();
        List<Lesson> tuesday = new ArrayList<>();
        List<Lesson> wednesday = new ArrayList<>();
        List<Lesson> thursday = new ArrayList<>();
        List<Lesson> friday = new ArrayList<>();
        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i);
            LocalDate thisDate = cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            for(Lesson l : allLessons) {
                LocalDate comparison = l.getStartTime().toLocalDateTime().toLocalDate();
                if(thisDate.equals(comparison)) {
                    switch(i) {
                        case Calendar.MONDAY -> monday.add(l);
                        case Calendar.TUESDAY -> tuesday.add(l);
                        case Calendar.WEDNESDAY -> wednesday.add(l);
                        case Calendar.THURSDAY -> thursday.add(l);
                        case Calendar.FRIDAY -> friday.add(l);
                    }
                }
            }
        }

        List<List<Lesson>> returnLessons = new ArrayList<>();
        returnLessons.add(monday);
        returnLessons.add(tuesday);
        returnLessons.add(wednesday);
        returnLessons.add(thursday);
        returnLessons.add(friday);

        return returnLessons;
    }

    public List<Schema> getWeekSchemaFormatted() {
        List<List<Lesson>> weekSchema = getWeekSchema();

        int indexOfLargest = ArrayTools.getIndexOfLargest(weekSchema);

        List<Schema> returnList = new ArrayList<>();
        for(int i = 0; i < weekSchema.get(indexOfLargest).size(); i++) {
            Schema s = new Schema();
            Lesson monday = (i < weekSchema.get(0).size() ) ? weekSchema.get(0).get(i) : new Lesson();
            s.setMonday(monday);
            Lesson tuesday = (i < weekSchema.get(1).size() ) ? weekSchema.get(1).get(i) : new Lesson();
            s.setTuesday(tuesday);
            Lesson wednesday = (i < weekSchema.get(2).size() ) ? weekSchema.get(2).get(i) : new Lesson();
            s.setWednesday(wednesday);
            Lesson thursday = (i < weekSchema.get(3).size() ) ? weekSchema.get(3).get(i) : new Lesson();
            s.setThursday(thursday);
            Lesson friday = (i < weekSchema.get(4).size() ) ? weekSchema.get(4).get(i) : new Lesson();
            s.setFriday(friday);
            returnList.add(s);
        }

        return returnList;
    }

    public Lesson getCurrentLesson() {
        LocalDateTime now = LocalDateTime.now();
        Lesson currentLesson = null;

        for(Lesson l : allLessons) {
            if(now.isAfter(l.getStartTime().toLocalDateTime()) && now.isBefore(l.getStopTime().toLocalDateTime()))
                currentLesson = l;
        }

        return currentLesson;
    }

    public List<Course> getUserCourses(Account a) throws SQLException {
        return dbAccess.getUserCourses(a);
    }

    public List<Course> getAllCourses() throws SQLException {
        return dbAccess.getAllCourses();
    }

    public List<Lesson> getLessonsInterval(LocalDate from, LocalDate to, Course course) {
        List<Lesson> returnList = new ArrayList<>();
        for(Lesson lesson : allLessons) {
            LocalDate start = lesson.getStartTime().toLocalDateTime().toLocalDate();
            if( (start.isAfter(from) || start.isEqual(from)) && (start.isBefore(to) || start.isEqual(to)) ) {
                if(course != null && course.getId() != -1) {
                    if(course.getName().equals(lesson.getCourseName())) {
                        returnList.add(lesson);
                    }
                } else
                    returnList.add(lesson);
            }
        }
        Collections.reverse(returnList);
        return returnList;
    }

    public Lesson getFirstLesson(Course c) {
        Lesson l = null;
        for(Lesson lesson : allLessons) {
            LocalDate start = lesson.getStartTime().toLocalDateTime().toLocalDate();

            if(c != null && c.getId() != -1) {
                if(c.getName().equals(lesson.getCourseName())) {
                    if(l == null)
                        l = lesson;
                    else {
                        if(start.isBefore(l.getStartTime().toLocalDateTime().toLocalDate()))
                            l = lesson;
                    }
                }
            }
        }
        return l;
    }

    public static boolean isInFuture(Lesson l) {
        return l.getStartTime().toLocalDateTime().isAfter(LocalDateTime.now());
    }
}
