package edu.skku.map.kidquiz;

public class UserInfo {
    private String username;
    private String password;
    private String Age;
    private String email;

    public UserInfo(String username, String password, String Age, String email) {
        this.username = username;
        this.password = password;
        this.Age = Age;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAge() {
        return Age;
    }

    public String getEmail() {
        return email;
    }
}
