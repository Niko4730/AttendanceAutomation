package DAL;

import BE.Account;
import BE.Course;
import BE.UserType;
import DAL.Server.MSSQLHandler;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

public class AccountDAL {
    private Connection conn;

    public AccountDAL() {
        conn = MSSQLHandler.getConnection();
    }

    public Account getAccountByUsername(String username) throws SQLException {
        String query = "SELECT Accounts.id, Accounts.username, Accounts.password, Accounts.type, UserInfo.firstName, UserInfo.lastName, UserInfo.email, UserInfo.phone FROM Accounts INNER JOIN UserInfo ON UserInfo.accountId = Accounts.id WHERE Accounts.username = ?";

        PreparedStatement execute = conn.prepareStatement(query);
        execute.setString(1,username);

        ResultSet rs = execute.executeQuery();

        if(!rs.next())
            return null;

        int id = rs.getInt("id");
        String user = rs.getString("username");
        String pass = rs.getString("password");
        UserType type = (rs.getInt("type") == 1) ? UserType.STUDENT : UserType.TEACHER;
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        int phone = rs.getInt("phone");

        Account returnAccount = new Account(user,pass,type,firstName,lastName,email,phone);
        returnAccount.setId(id);

        return returnAccount;
    }

    public Account getAccountById(int idd) throws SQLException {
        String query = "SELECT Accounts.id, Accounts.username, Accounts.password, Accounts.type, UserInfo.firstName, UserInfo.lastName, UserInfo.email, UserInfo.phone FROM Accounts INNER JOIN UserInfo ON UserInfo.accountId = Accounts.id WHERE Accounts.id = ?";

        PreparedStatement execute = conn.prepareStatement(query);
        execute.setInt(1,idd);

        ResultSet rs = execute.executeQuery();

        if(!rs.next())
            return null;

        int id = rs.getInt("id");
        String user = rs.getString("username");
        String pass = rs.getString("password");
        UserType type = (rs.getInt("type") == 1) ? UserType.STUDENT : UserType.TEACHER;
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        int phone = rs.getInt("phone");

        Account returnAccount = new Account(user,pass,type,firstName,lastName,email,phone);
        returnAccount.setId(id);

        return returnAccount;
    }

    public int saveNewAccount(Account a) throws SQLException {
        String query = "INSERT INTO Accounts (username,password,type) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,a.getUsername());
        ps.setString(2,a.getPassword());
        ps.setInt(3,(a.getType() == UserType.STUDENT) ? 1 : 2);

        int rows = ps.executeUpdate();

        if(rows == 0)
            return -1;

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) {
            int newId = rs.getInt(1);

            String query2 = "INSERT INTO UserInfo (accountId,firstName,lastName,email,phone) VALUES (?,?,?,?,?)";
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setInt(1,newId);
            ps2.setString(2,a.getFirstName());
            ps2.setString(3,a.getLastName());
            ps2.setString(4,a.getEmail());
            ps2.setInt(5,a.getPhone());

            int rows2 = ps2.executeUpdate();

            if(rows2 == 0)
                return -1;

            return newId;
        }
        else
            return -1;
    }

    public void updateAccount(Account a) throws SQLException {
        int accountId = a.getId();
        String query1 = "UPDATE Accounts SET password = ? WHERE id = ?;";
        String query2 = "UPDATE UserInfo SET firstName = ?, lastName = ?, email = ?, phone = ? WHERE accountId = ?;";
        PreparedStatement ps1 = conn.prepareStatement(query1);
        ps1.setString(1,a.getPassword());
        ps1.setInt(2,a.getId());
        PreparedStatement ps2 = conn.prepareStatement(query2);
        ps2.setString(1,a.getFirstName());
        ps2.setString(2,a.getLastName());
        ps2.setString(3,a.getEmail());
        ps2.setInt(4,a.getPhone());
        ps2.setInt(5,a.getId());

        ps1.executeUpdate();
        ps2.executeUpdate();
    }

    public void addUserCourses(Account a, List<Course> coursesToAdd) throws SQLException {
        for(Course c : coursesToAdd) {
            String query = "INSERT INTO UserCourses (accountId,courseId) VALUES(?,?);";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,a.getId());
            ps.setInt(2,c.getId());
            ps.executeUpdate();
        }
    }
}
