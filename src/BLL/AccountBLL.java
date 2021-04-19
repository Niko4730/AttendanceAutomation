package BLL;

import BE.Account;
import BE.Course;
import DAL.AccountDAL;
import DAL.AttendanceDAL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountBLL {
    private AccountDAL dbAccess;

    private List<Account> allAccounts;

    public AccountBLL() {
        dbAccess = new AccountDAL();
    }

    public boolean usernameExists(String username) throws SQLException {
        Account check = dbAccess.getAccountByUsername(username);
        return check != null;
    }

    public Account checkLogin(String username, String password) throws SQLException {
        if(!username.isEmpty() && !password.isEmpty()) {
            Account check = dbAccess.getAccountByUsername(username);

            if(check == null) return null;

            return (check.getPassword().equals(password)) ? check : null;
        } else
            return null;
    }

    public boolean newAccount(Account a) throws SQLException {
        int newId = dbAccess.saveNewAccount(a);

        if(newId == -1)
            return false;

        a.setId(newId);

        return true;
    }

    public void updateAccount(Account a) throws SQLException {
        dbAccess.updateAccount(a);
    }

    public Account getAccountFromId(int id) throws SQLException {
        return dbAccess.getAccountById(id);
    }

    public void addUserCourses(Account a, List<Course> coursesToAdd) throws SQLException {
        dbAccess.addUserCourses(a, coursesToAdd);
    }
}
