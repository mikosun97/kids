package edu.skku.map.kidquiz;

import java.util.HashMap;
import java.util.Map;

public class firebasePost_score {

    public String usermail;
    public int a_right;
    public int a_wrong;
    public int b_right;
    public int b_wrong;

    public firebasePost_score(String usermail, int a_right, int a_wrong, int b_right, int b_wrong){
        this.usermail=usermail;
        this.a_right=a_right;
        this.a_wrong=a_wrong;
        this.b_right=b_right;
        this.b_wrong=b_wrong;

    }
    public firebasePost_score(){

    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("usermail", usermail);
        result.put("math_a_right", a_right);
        result.put("math_a_wrong", a_wrong);
        result.put("math_b_right", b_right);
        result.put("math_b_wrong", b_wrong);


        return result;
    }

}



