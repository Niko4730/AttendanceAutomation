package BLL;

import BE.Account;
import BE.Attendance;
import BE.Course;
import BE.Lesson;
import DAL.AttendanceDAL;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AttendanceBLL {
    private AttendanceDAL dbAccess;
    private List<Attendance> allAttendance;

    public AttendanceBLL(Account a) throws SQLException {
        dbAccess = new AttendanceDAL();
        allAttendance = new ArrayList<>();

        allAttendance.addAll(dbAccess.getAllAttendance());
    }

    public boolean hasAttended(Account a, Lesson l) {
        return allAttendance.contains(new Attendance(a,l));
    }

    public void setAttended(Account a, Lesson l) throws SQLException {
        if(hasAttended(a,l)) return;

        Attendance at = new Attendance(a,l);

        allAttendance.add(at);

        dbAccess.saveAttendance(at);
    }

    public void removeAttendance(Account a, Lesson l) throws SQLException {
        if(!hasAttended(a,l)) return;

        Attendance at = new Attendance(a,l);

        allAttendance.remove(at);

        dbAccess.removeAttendance(at);
    }

    public List<Attendance> getAllAttendance() {
        return allAttendance;
    }

    public List<Attendance> getAttendanceInterval(LocalDate from, LocalDate to, Course course) {
        List<Attendance> returnList = new ArrayList<>();
        for(Attendance a : allAttendance) {
            Lesson lesson = a.getLesson();
            LocalDate start = lesson.getStartTime().toLocalDateTime().toLocalDate();
            if( (start.isAfter(from) || start.isEqual(from)) && (start.isBefore(to) || start.isEqual(to)) ) {
                if(course != null) {
                    if(course.getName().equals(lesson.getCourseName())) {
                        returnList.add(a);
                    }
                } else
                    returnList.add(a);
            }
        }

        return returnList;
    }

    public double getAllCourseAttendances(List<Account> a, Course c, List<Lesson> lessons) {
        double totalLessons = 0;
        double totalAttended = 0;

        for(Account acc : a) {
            for (Lesson l : lessons) {
                if (c.getId() != -1) {
                    if (l.getCourseName().equals(c.getName())) {
                        totalLessons++;
                        if (hasAttended(acc, l))
                            totalAttended++;
                    }
                } else {
                    totalLessons++;
                    if (hasAttended(acc, l))
                        totalAttended++;
                }
            }
        }

        return (totalAttended / totalLessons) * 100;
    }

    public double getCourseAttendance(Account a, Course c, List<Lesson> lessons, List<Attendance> attendances) {
        double totalLessons = 0;
        double totalAttended = 0;

        for(Lesson l : lessons) {
            if(c.getId() != -1) {
                if (l.getCourseName().equals(c.getName())) {
                    totalLessons++;
                    if (hasAttended(a, l))
                        totalAttended++;
                }
            } else {
                totalLessons++;
                if(hasAttended(a,l))
                    totalAttended++;
            }
        }

        return (totalAttended / totalLessons) * 100;
    }

    public double[] getAllDaysAbsence(List<Account> a, Course c, List<Lesson> lessons) {
        double[] totalLessons = {0,0,0,0,0};
        double[] totalAttended = {0,0,0,0,0};

        for(Account acc : a) {
            for(Lesson l : lessons) {
                int weekIndex = l.getStartTime().toLocalDateTime().toLocalDate().getDayOfWeek().getValue() - 1;
                if(c.getId() != -1) {
                    if(l.getCourseName().equals(c.getName())) {
                        totalLessons[weekIndex]++;
                        if(hasAttended(acc,l))
                            totalAttended[weekIndex]++;
                    }
                } else {
                    totalLessons[weekIndex]++;
                    if(hasAttended(acc,l))
                        totalAttended[weekIndex]++;
                }
            }
        }

        double[] absence = new double[5];
        for(int i = 0; i < totalLessons.length; i++) {
            double att = (totalAttended[i] / totalLessons[i]) * 100;
            att = Double.isNaN(att) ? 0 : att;
            absence[i] = 100 - att;
        }

        return absence;
    }

    public double[] getDaysAbsence(Account a, Course c, List<Lesson> lessons, List<Attendance> attendances) {
        double[] totalLessons = {0,0,0,0,0};
        double[] totalAttended = {0,0,0,0,0};

        for(Lesson l : lessons) {
            int weekIndex = l.getStartTime().toLocalDateTime().toLocalDate().getDayOfWeek().getValue() - 1;
            if(c.getId() != -1) {
                if(l.getCourseName().equals(c.getName())) {
                    totalLessons[weekIndex]++;
                    if(hasAttended(a,l))
                        totalAttended[weekIndex]++;
                }
            } else {
                totalLessons[weekIndex]++;
                if(hasAttended(a,l))
                    totalAttended[weekIndex]++;
            }
        }

        double[] absence = new double[5];
        for(int i = 0; i < totalLessons.length; i++) {
            double att = (totalAttended[i] / totalLessons[i]) * 100;
            att = Double.isNaN(att) ? 0 : att;
            absence[i] = 100 - att;
        }

        return absence;
    }
    public double[] getAllDaysAbsence(List<Account> a, Course c, List<Lesson> lessons) {
        double[] totalLessons = {0,0,0,0,0};
        double[] totalAttended = {0,0,0,0,0};

        for(Account acc : a) {
            for(Lesson l : lessons) {
                int weekIndex = l.getStartTime().toLocalDateTime().toLocalDate().getDayOfWeek().getValue() - 1;
                if(c.getId() != -1) {
                    if(l.getCourseName().equals(c.getName())) {
                        totalLessons[weekIndex]++;
                        if(hasAttended(acc,l))
                            totalAttended[weekIndex]++;
                    }
                } else {
                    totalLessons[weekIndex]++;
                    if(hasAttended(acc,l))
                        totalAttended[weekIndex]++;
                }
            }
        }

        double[] absence = new double[5];
        for(int i = 0; i < totalLessons.length; i++) {
            double att = (totalAttended[i] / totalLessons[i]) * 100;
            att = Double.isNaN(att) ? 0 : att;
            absence[i] = 100 - att;
        }

        return absence;
    }

    public double getTotalAttendance(Course c, LocalDate from, LocalDate to) throws SQLException {
        List<Account> students = getAccountsFromCourse(c);

        double sumAttendance = 0;
        for(Account a : students) {
            SchemaBLL sBLL = new SchemaBLL(a);
            AttendanceBLL aBLL = new AttendanceBLL(a);

            List<Lesson> lessons = sBLL.getLessonsInterval(from,to,c);
            List<Attendance> attendances = aBLL.getAttendanceInterval(from,to,c);

            double addAttendance = getCourseAttendance(a,c,lessons,attendances);

            sumAttendance += getCourseAttendance(a,c,lessons,attendances);
        }

        return sumAttendance / students.size();
    }

    public List<Account> getAccountsFromCourse(Course c) throws SQLException {
        return dbAccess.getAccountsFromCourse(c);
    }
}
