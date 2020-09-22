package edu.skku.map.kidquiz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import java.util.Random;

public class Math extends AppCompatActivity implements AIListener {
    private static final int PERMISSION_REQUEST_AUDIO = 0;
    private AIConfiguration config;
    private AIService aiService;

    Random rnd;
    //For TTS
    private static TextToSpeech textToSpeech;
    private DatabaseReference mPostReference;
    private StorageReference mStorageRef;

    Button next_prob;
    Button end_quiz;

    Button answerButton;

    TextView vocabq; //my query
    TextView vocaba;
    TextView number_b1;
    TextView number_b2;
    TextView operator;
    TextView number;

    int score;
    int num_probs;
    int cur_prob;
    int answer;
    int answer1;
    int answer2;
    int operation;
    int type;

    String my_answer;
    String usermail;
    View.OnClickListener answerListener;
    View.OnClickListener nextListener;
    View.OnClickListener finishListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);
        //reset score
        score=0;
        num_probs=2;
        cur_prob=1;
        //FirebaseApp.initializeApp(this);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //set buttons
        answerButton = (Button)findViewById(R.id.vocab_answer);

        if(getIntent().getExtras() != null){
            Intent mathIntent = getIntent();
            usermail = mathIntent.getStringExtra("usermail");
        }
        Log.d("math start","key "+usermail);

        nextListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //num_probs+=1;
                my_answer=null;
                if(cur_prob<=num_probs){
                    rnd = new Random();
                    type = rnd.nextInt(2);
                    if(type == 0){
                        loadproblem1(cur_prob);
                    } else {
                        loadproblem2(cur_prob);
                    }
                    answerButton.setText("누른 후에 답을 말해주세요");
                    vocaba.setText("");
                    vocabq.setText("");
                }else{
                    //quiz Complete
                }

            }
        };
        finishListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Math.this, "Ending Quiz", Toast.LENGTH_SHORT).show();
                Intent scoreIntent = new Intent(Math.this, GetScore.class);
                scoreIntent.putExtra("score", score);
                scoreIntent.putExtra("num_probs",num_probs);
                scoreIntent.putExtra("quiz_type","Math");
                scoreIntent.putExtra("usermail",usermail);
                startActivity(scoreIntent);
            }
        };


        //Load first question
        my_answer=null;
        rnd = new Random();
        type = rnd.nextInt(2);
        if(type == 0){
            loadproblem1(cur_prob);
        } else {
            loadproblem2(cur_prob);
        }
        //download

        //dialogflow
        vocabq= (TextView)findViewById(R.id.vocab_q);
        vocaba= (TextView)findViewById(R.id.vocab_a);
        vocaba.setText("");
        vocabq.setText("");

        //kidquiz 6e0311eb15d244e0bfd8bab3dfebd68c
        //holiday 0e561656f7f146698b8323593259b907
        config = new AIConfiguration("6e0311eb15d244e0bfd8bab3dfebd68c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        answerListener=new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //requestAudioPermission();

                listen();
            }
        };
        answerButton.setOnClickListener(answerListener);

        //tts
        //setup tts
        if(textToSpeech==null){
            textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status==TextToSpeech.SUCCESS){
                        int result=textToSpeech.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(Math.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            //음성 톤
                            textToSpeech.setPitch(1f);
                            //읽는 속도
                            textToSpeech.setSpeechRate(0.8f);
                        }
                    }

                }
            });
        }
    }
    private void listen() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Listening Start","Listening");
            //answerButton.setText("듣는중이에요~");
            aiService.startListening();
        } else {
            // Permission is missing and must be requested.
            Log.d("Listening Start","Need Permission");
            requestAudioPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResult(AIResponse response) {
        answerButton.setText("정답 확인중");
        Result result = response.getResult();
        my_answer=result.getResolvedQuery();
        Log.d("Select Response","Response"+my_answer);
        vocaba.setText(String.valueOf(String.valueOf(answer)));
        vocabq.setText(my_answer);
        //check answer
        if(my_answer.equals(String.valueOf(answer))){
            score++;
            Toast.makeText(Math.this, "정답입니다!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Math.this, "오답입니다 ㅠㅠ", Toast.LENGTH_SHORT).show();
        }
        cur_prob++;
        if(cur_prob>num_probs){
            answerButton.setOnClickListener(finishListener);
            answerButton.setText("퀴즈 종료하기");
        }else {
            answerButton.setOnClickListener(nextListener);
            answerButton.setText("다음 문제로~");
        }
        //tts
        //textToSpeech.speak(result.getFulfillment().getSpeech(),TextToSpeech.QUEUE_FLUSH,null,"test");
        textToSpeech.speak(String.valueOf(answer),TextToSpeech.QUEUE_FLUSH,null,"test");

    }

    @Override
    public void onError(AIError error) {
        Log.d("Select Button", "Listening error: " + error.getMessage());
        answerButton.setText("잘 못들었어요 다시 말해주세요! :(");
        answerButton.setOnClickListener(answerListener);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        answerButton.setText("듣는중이에요~");
    }

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {
        answerButton.setText("듣기 완료!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_AUDIO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aiService.startListening();
            } else {
                Log.d("request result", "permission denied");
            }
        }
    }

    private void requestAudioPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(findViewById(R.id.select_container), getString(R.string.permission_text_audio),
                    Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(Math.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_AUDIO);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
        }
    }
    //get problem
    public void loadproblem1(int prob_num){
        final ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                VocabProb get = dataSnapshot.getValue(VocabProb.class);
                //answer=get.getAnswer();
                rnd = new Random();
                number_b1 = (TextView) findViewById(R.id.number1);
                number_b2 = (TextView) findViewById(R.id.number3);

                operator = (TextView) findViewById(R.id.number2);
                rnd = new Random();
                operation = rnd.nextInt(4);
                if(operation == 0){
                    operator.setText("+");

                    answer1 = rnd.nextInt(100);
                    number_b1.setText(String.valueOf(answer1));

                    answer2 = rnd.nextInt(10);
                    number_b2.setText(String.valueOf(answer2));

                    answer = answer1 + answer2;
                }
                else if(operation == 1){
                    operator.setText("+");

                    answer1 = rnd.nextInt(10);
                    number_b1.setText(String.valueOf(answer1));

                    answer2 = rnd.nextInt(100);
                    number_b2.setText(String.valueOf(answer2));

                    answer = answer1 + answer2;
                }
                else if(operation == 2){
                    operator.setText("-");

                    answer1 = rnd.nextInt(100);
                    number_b1.setText(String.valueOf(answer1));

                    answer2 = rnd.nextInt(10);
                    number_b2.setText(String.valueOf(answer2));

                    answer = answer1 - answer2;
                }
                else{
                    operator.setText("*");

                    answer1 = rnd.nextInt(10);
                    number_b1.setText(String.valueOf(answer1));

                    answer2 = rnd.nextInt(10);
                    number_b2.setText(String.valueOf(answer2));

                    answer = answer1 * answer2;
                }

                answerButton.setOnClickListener(answerListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mPostReference.child("vocab_probs/prob"+prob_num).addValueEventListener(postListener);
        answerButton.setOnClickListener(answerListener);
    }

    public void loadproblem2(int prob_num){
        final ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                VocabProb get = dataSnapshot.getValue(VocabProb.class);
                //answer=get.getAnswer();

                number = (TextView) findViewById(R.id.number2);
                number_b1 = (TextView) findViewById(R.id.number1);
                number_b2= (TextView) findViewById(R.id.number3);
                rnd = new Random();
                answer = rnd.nextInt(1000);
                number.setText(String.valueOf(answer));
                number_b1.setText("");
                number_b2.setText("");
                answerButton.setOnClickListener(answerListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mPostReference.child("vocab_probs/prob"+prob_num).addValueEventListener(postListener);
        answerButton.setOnClickListener(answerListener);
    }





}

//firebase get image