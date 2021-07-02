package com.arviet.arproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.arviet.arproject.adapter.LessonAdapter;
import com.arviet.arproject.api.ApiService;
import com.arviet.arproject.model.Lesson;
import com.arviet.arproject.model.Marker;
import com.arviet.arproject.model.AddLessonDialog;
import com.arviet.arproject.responseModel.GetMarkerListResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonListAcitivity extends AppCompatActivity {
    ListView listViewLesson;
    ImageView imvBack;
    List<Lesson> lessonArr;
    Button btnAddLesson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        // Get access token
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("ACCESS_TOKEN",null);
        String username = preferences.getString("username",null);
        listViewLesson = (ListView) findViewById(R.id.listView_lesson);
        imvBack = findViewById(R.id.imageViewBack);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonListAcitivity.this,HomePageActivity.class));
            }
        });
        getLessonList(username,accessToken);
        btnAddLesson = (Button) findViewById(R.id.btn_addLesson);
        btnAddLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLessonDialog dialog = new AddLessonDialog();
                dialog.setContext(LessonListAcitivity.this);
                dialog.show(getSupportFragmentManager(),"MyCustomFragment");
            }
        });
    }

    private void getLessonList(String username, String accessToken) {
        ApiService.apiService.getLessonList(username,"Bearer "+accessToken).enqueue(new Callback <List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                lessonArr = new ArrayList<>();
                lessonArr = response.body();
                LessonAdapter adapter = new LessonAdapter(
                        LessonListAcitivity.this,R.layout.line_lesson,lessonArr
                );
                listViewLesson.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listViewLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String lessonID = ""+lessonArr.get(position).lessonID;
                        Log.i("hahaha",lessonID);
                        ApiService.apiService.getMarkerList(lessonID).enqueue(new Callback<GetMarkerListResponse>() {
                            @Override
                            public void onResponse(Call<GetMarkerListResponse> call, Response<GetMarkerListResponse> response) {
                                Log.i("dkm",response.body().toString());
                                GetMarkerListResponse getMarkerListResponse = response.body();
                                if (getMarkerListResponse.getStatus().equals("success")){
                                    List<Marker> markerList =getMarkerListResponse.getMarkerList();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LessonListAcitivity.this);
                                    String listMarkerJsonString = new Gson().toJson(markerList);
                                    preferences.edit().putString("markerlist",listMarkerJsonString).apply();
                                    preferences.edit().commit();
                                    startActivity(new Intent(LessonListAcitivity.this,LearnActivity.class));
                                }
                                else{
                                    Toast.makeText(LessonListAcitivity.this,"Bài giảng không có nội dung",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<GetMarkerListResponse> call, Throwable t) {
                                Toast.makeText(LessonListAcitivity.this,"Lỗi không xác định",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
//                Toast.makeText(LessonListAcitivity.this,"Call Api sucess",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                Toast.makeText(LessonListAcitivity.this,"Lỗi không xác định",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ThemBaiGiang(Lesson lesson){
        lessonArr.add(lesson);
    }
}