package com.ayman.hblik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionStatsActivity extends AppCompatActivity {
    String question, option_1, option_2, option_3, option_4,option_5;
    int option1, option2,option3, option4, option5,id_question,answerNbr,score;
    TextView mQuestion,mOption1,mOption2,mOption3,mOption4,mOption5,bar1,bar2,bar3,bar4,bar5;
    private static final String TAG = "QuestionStatsActivity";
BottomNavigationItemView askMi;
    TextView mName,mScore;
    CircleImageView imageView;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_stats);
        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        final String id_user=preferences.getString("id_user",null);
        String firstName =preferences.getString("first_name",null);
        String lastName =preferences.getString("last_name",null);
        score =preferences.getInt("score",0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);

        View v0 = navigationView.getHeaderView(0);
        imageView = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mScore = findViewById(R.id.score);
        mName.setText(firstName+" "+lastName);
        mScore.setText("score : "+score);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_settings:

                        return true;
                    case R.id.nav_help:
                        return true;
                    case R.id.nav_report:
                        return true;
                    case R.id.nav_share:
                        return true;
                    case R.id.nav_rate_us:
                        return true;
                    case R.id.nav_log_out:
                        return true;
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        askMi=findViewById(R.id.nav_ask);
        if(score <25){
            askMi.setEnabled(false);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:

                        return true;
                    case R.id.nav_answer:
                        Intent j = new Intent(QuestionStatsActivity.this, QuestionsActivity.class);
                        startActivity(j);
                        return true;
                    case R.id.nav_ask:
                        if (score < 25) {
                            Toast.makeText(QuestionStatsActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(QuestionStatsActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                        }

                        return true;

                        case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(QuestionStatsActivity.this, UserActivityActivity.class);
                        Bundle b =new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });


        setProfilePhoto(id_user);


        Bundle b = getIntent().getExtras();
        answerNbr = b.getInt("answerNbr");
        question = b.getString("question");
        id_question = b.getInt("id_question");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("questions")
                .whereEqualTo("id", id_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                option1 = document.getLong("option_1").intValue();
                                option2 = document.getLong("option_2").intValue();
                                if (document.getLong("option_3") != null) {
                                    option3 = document.getLong("option_3").intValue();
                                }
                                if (document.getLong("option_4") != null) {
                                    option4 = document.getLong("option_4").intValue();
                                }
                                if (document.getLong("option_5") != null) {
                                    option5 = document.getLong("option_5").intValue();
                                }

                                option_1 = document.getString("option1");
                                option_2 = document.getString("option2");
                                option_3 = document.getString("option3");
                                option_4 = document.getString("option4");
                                option_5 = document.getString("option5");

                                break;
                            }

                            fillContent();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });


    }
    private void setProfilePhoto(String id){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START download_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("profilePhotos/"+id+".png");


        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
// Set the Bitmap data to the ImageView
                imageView.setImageBitmap(bmp);            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }

        });

    }

    private void fillContent() {
        mOption1=findViewById(R.id.option1);
        mOption2=findViewById(R.id.option2);
        mOption3=findViewById(R.id.option3);
        mOption4=findViewById(R.id.option4);
        mOption5=findViewById(R.id.option5);
        bar1=findViewById(R.id.barQ1);
        bar2=findViewById(R.id.barQ2);
        bar3=findViewById(R.id.barQ3);
        bar4=findViewById(R.id.barQ4);
        bar5=findViewById(R.id.barQ5);
        mQuestion=findViewById(R.id.questionII);

        mQuestion.setText(question);
        mOption1.setText(option_1);
        mOption2.setText(option_2);



        Log.d(TAG, "fillContent: "+option1+"    "+answerNbr);
        if(answerNbr== 0){
            changeWidth(bar1,0);
            changeWidth(bar2,0);
        }
        else {
            changeWidth(bar1,option1);
            changeWidth(bar2,option2);
        }

        if(option_3 != null) {
            mOption3.setVisibility(View.VISIBLE);
            bar3.setVisibility(View.VISIBLE);

            mOption3.setText(option_3);
            if(answerNbr== 0){
                changeWidth(bar3,0);
            }
            else {
                     changeWidth(bar3,option3);            }
        }
        else
            mOption3.setVisibility(View.GONE);

        if(option_4 != null) {
            mOption4.setVisibility(View.VISIBLE);
            bar4.setVisibility(View.VISIBLE);

            mOption4.setText(option_4);
            if(answerNbr== 0){
                changeWidth(bar4,0);
            }
            else {

                changeWidth(bar4,option4);            }
        }
        else
            mOption4.setVisibility(View.GONE);

        if(option_5 != null) {
            mOption5.setVisibility(View.VISIBLE);
            bar5.setVisibility(View.VISIBLE);
            mOption5.setText(option_5);
            if(answerNbr== 0){
                changeWidth(bar5,0);
            }
            else {
                       changeWidth(bar5,option5);
            }
        }
        else
            mOption5.setVisibility(View.GONE);

    }

    private void changeWidth(TextView view,int option){
        float width = view.getLayoutParams().width;

        float newWidth=width*((float)option/(float)answerNbr);

        Log.d(TAG, "changeWidth: "+width+"   "+newWidth+"   "+ (int)newWidth
        +"   option : "+option+"  ansN : "+answerNbr);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)newWidth,  100);
        view.setLayoutParams(layoutParams);
    }
}
