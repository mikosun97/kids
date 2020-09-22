package edu.skku.map.kidquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class GetScore extends AppCompatActivity {
    private String Age;
    private String Password;
    Button try_again;
    Button go_home;
    TextView score_txt;
    int score;
    int num_probs;
    int a_score;
    int a_probs;
    int b_score;
    int b_probs;
    String usermail;
    String quiz_type;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        if(getIntent().getExtras() != null){
            Intent scoreIntent = getIntent();
            score = scoreIntent.getIntExtra("score",0);
            num_probs = scoreIntent.getIntExtra("num_probs",0);
            quiz_type=scoreIntent.getStringExtra("quiz_type");
            usermail= scoreIntent.getStringExtra("usermail");
            //TextView name = (TextView) findViewById((R.id.drawer_username));
            //name.setText(username);
        }
        //set score text
        Log.d("score page","Key"+usermail);
        score_txt=(TextView)findViewById(R.id.score_txt);
        score_txt.setText("맞춘 개수: "+score+", 총 개수: "+num_probs);

        a_score = score;
        b_score = score;
        a_probs = num_probs;
        b_probs = num_probs;

        try_again = (Button)findViewById(R.id.score_again);
        go_home = (Button)findViewById(R.id.score_fin);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFirebaseDatabase(true);

                if (quiz_type.equals("math")){
                    Intent mathIntent = new Intent(GetScore.this, Math.class);
                    mathIntent.putExtra("Username", usermail);
                    postFirebaseDatabase(true);
                    startActivity(mathIntent);
                }else{
                    Intent wordIntent = new Intent(GetScore.this, word.class);
                    wordIntent.putExtra("Username", usermail);
                    postFirebaseDatabase(true);
                    startActivity(wordIntent);
                }
            }
        });
        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFirebaseDatabase(true);

//              Toast.makeText(GetScore.this, "Going Home", Toast.LENGTH_SHORT).show();
                Intent gohomeIntent = new Intent(GetScore.this, SelectQuiz.class);
                gohomeIntent.putExtra("Username", usermail);
                startActivity(gohomeIntent);
            }
        });
    }
/*
    mPostReference = FirebaseDatabase.getInstance().getReference();

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                String key = postSnapshot.getKey();
                Log.d("get loop", "get loop: " + key + " "+usermail);
                if(key.equals(usermail)){
                    for(DataSnapshot childSnapshot: postSnapshot.getChildren()){
                        int get= childSnapshot.getValue(int.class);
                        String c_key=childSnapshot.getKey();
                        if(c_key.equals("a_right")) {
                            int now_a_right = get;
                            a_score += now_a_right;
                        }else if(c_key.equals("a_wrong")){
                            int now_a_wrong = get;
                            a_probs += a_score + now_a_wrong;
                        }else if(c_key.equals("b_right")){
                            int now_b_right = get;
                            b_score += now_b_right;
                        }else if(c_key.equals("b_wrong")){
                            int now_b_wrong = get;
                            b_probs += now_b_wrong;
                        }
                        //Log.d("got","got"+get);

                        //info_s = new String[]{username, get.password, get.fullname, get.birthday, get.email};
                    }

                    //Log.d("getFirebaseDatabase", "Key: " + key);
                    //Log.d("getFirebaseDatabase", "info: " + info_s[2] + info_s[3] + info_s[4]);
                    break;
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("getFirebaseDatabase", "Key");
        }
    };
    mPostReference.child("history_list").addValueEventListener(postListener);


 */

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
                //Log.d("got","got"+get);

                //info_s = new String[]{username, get.password, get.fullname, get.birthday, get.email};
            firebasePost_score post = new firebasePost_score(usermail, a_score, a_probs, b_score, b_probs);
            postValues = post.toMap();
        }
        Log.d("posting", "Key: " + usermail);
        childUpdates.put("/history_list/" + usermail +"/"+ quiz_type, postValues);
        mPostReference.updateChildren(childUpdates);
        //clearET();
    }


}
