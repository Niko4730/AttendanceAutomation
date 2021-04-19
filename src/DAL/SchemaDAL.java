package DAL;

import BE.Account;
import BE.Course;
import BE.Lesson;
import DAL.Server.MSSQLHandler;
import javafx.collections.FXCollections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchemaDAL {
    private Connection conn;

    public SchemaDAL() {
        conn = MSSQLHandler.getConnection();
    }

    public Lesson getLessonById(int id) throws SQLException {
        String query = "SELECT Lessons.id,Lessons.start,Lessons.stop,Courses.courseName FROM Lessons INNER JOIN Courses ON Courses.id = Lessons.courseId WHERE Lessons.id = ?;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();

        if(rs.next())
            return new Lesson(rs.getInt("id"),rs.getString("courseName"),rs.getTimestamp("start"),rs.getTimestamp("stop"));
        else
            return null;
    }

    public List<Lesson> getAllLessons(Account a) throws SQLException {
        String query = "SELECT Courses.id, Courses.courseName FROM UserCourses INNER JOIN Courses ON Courses.id = UserCourses.courseId WHERE UserCourses.accountId = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,a.getId());
        ResultSet rs = ps.executeQuery();

        List<Lesson> lessons = new ArrayList<>();
        while(rs.next()) {
            int courseId = rs.getInt("id");
            String courseName = rs.getString("courseName");

            String query2 = "SELECT * FROM Lessons WHERE courseId = ?";
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setInt(1,courseId);

            ResultSet rs2 = ps2.executeQuery();

            while(rs2.next()) {
                int id = rs2.getInt("id");
                Timestamp start = rs2.getTimestamp("start");
                Timestamp stop = rs2.getTimestamp("stop");

                Lesson l = new Lesson(id,courseName,start,stop);

                lessons.add(l);
            }
        }

        lessons.sort( (l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()) );

        return lessons;
    }

    public List<Course> getUserCourses(Account a) throws SQLException {
        String query = "SELECT UserCourses.courseId, Courses.courseName FROM UserCourses INNER JOIN Courses ON Courses.id = UserCourses.courseId WHERE UserCourses.accountId = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,a.getId());
        ResultSet rs = ps.executeQuery();

        List<Course> returnList = new ArrayList<>();
        while(rs.next()) {
            returnList.add(new Course(rs.getInt("courseId"),rs.getString("courseName")));
        }

        return returnList;
    }

    public List<Course> getAllCourses() throws SQLException {
        String query = "SELECT * FROM Courses;";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        List<Course> returnList = new ArrayList<>();
        while(rs.next()) {
            returnList.add(new Course(rs.getInt("id"),rs.getString("courseName")));
        }

        return returnList;
    }
}
