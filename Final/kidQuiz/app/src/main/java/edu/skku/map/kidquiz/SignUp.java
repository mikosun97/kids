package edu.skku.map.kidquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String username = "", password = "", age = "", email = "";
    String sort = "username";
    EditText Usernameedit, Passwordedit, Ageedit, Emailedit;
    Button button;
    //ListView datalist;
    ArrayList<UserInfo> memos;
    UserInfoAdapter adapter;
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<UserInfo> memo_data;
    //int checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        data = new ArrayList<String>();
        memo_data = new ArrayList<UserInfo>();

        Usernameedit = (EditText) findViewById(R.id.signupUsername);
        Passwordedit = (EditText) findViewById(R.id.signupPassword);
        Ageedit = (EditText) findViewById(R.id.signupAge);
        Emailedit = (EditText) findViewById(R.id.signupEmail);
        button = (Button) findViewById(R.id.signupButton);

        memos = new ArrayList<UserInfo>();
        adapter = new UserInfoAdapter(this, memos);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = Usernameedit.getText().toString();
                password = Passwordedit.getText().toString();
                age = Ageedit.getText().toString();
                email = Emailedit.getText().toString();

                if ((username.length() * password.length() * age.length() * email.length()) == 0) {
                    Toast.makeText(SignUp.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                }
                else {
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null){
                                Log.d("key exists","key "+email);
                                Toast.makeText(SignUp.this, "Please use another email", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d("new key","key "+email);

                                Toast.makeText(SignUp.this, "Posting!", Toast.LENGTH_SHORT).show();
                                postFirebaseDatabase(true);
                                Intent signupIntent = new Intent(SignUp.this, MainActivity.class);
                                signupIntent.putExtra("Email", email);
                                startActivity(signupIntent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("getFirebaseDatabase", "Key");
                        }
                    };
                    mPostReference.child("user_list").child(email).addValueEventListener(postListener);
                }
            }
        });
        Log.d("onDataChange", "Set onclick complete");
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            firebasePost post = new firebasePost(username, password, age, email);
            postValues = post.toMap();
        }
        Log.d("posting", "Key: " + email);
        childUpdates.put("/user_list/" + email, postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET() {
        if(Usernameedit.length()>0){
            Usernameedit.setText("");
        }
        if(Passwordedit.length()>0){
            Passwordedit.setText("");
        }
        if(Ageedit.length()>0){
            Ageedit.setText("");
        }
        if(Emailedit.length()>0){
            Emailedit.setText("");
        }

        username = "";
        password = "";
        age = "";
        email = "";
    }

}
