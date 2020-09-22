package edu.skku.map.kidquiz;

import java.util.HashMap;
import java.util.Map;

public class firebasePost {

    public String username;
    public String password;
    public String age;
    public String email;


    public firebasePost(String username, String password, String age, String email){
        this.username = username;
        this.password = password;
        this.age = age;
        this.email = email;
    }
    public firebasePost(){

    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Username", username);
        result.put("Password", password);
        result.put("Age", age);
        result.put("Email", email);

        return result;
    }

}



