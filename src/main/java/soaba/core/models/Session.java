package soaba.core.models;

import java.util.Calendar;
import java.util.Date;

import com.stormpath.sdk.account.Account;

import flexjson.JSON;

public class Session {
    @JSON(include = false)
    private Account userAccount;
    private String username;
    private String givenName;
    private String middleName;
    private String surname;
    private String token;
    private Date expirationDate;

    public Session() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        this.setExpirationDate(c.getTime());
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Account getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
        this.username = userAccount.getUsername();
        this.setGivenName(userAccount.getGivenName());
        this.setMiddleName(userAccount.getMiddleName());
        this.setSurname(userAccount.getSurname());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
