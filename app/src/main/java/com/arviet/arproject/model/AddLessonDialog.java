package com.arviet.arproject.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.arviet.arproject.LessonListAcitivity;
import com.arviet.arproject.R;
import com.arviet.arproject.api.ApiService;
import com.arviet.arproject.requestModel.AddLessonRequest;
import com.arviet.arproject.responseModel.AddLessonResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLessonDialog extends DialogFragment {
    private EditText edtLessonID;
    private Button btn_addLesson;
    private Button btn_Cancel;
    private static final String TAG="AddLessonDialog";
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_lesson, container,false);
        btn_addLesson = (Button) view.findViewById(R.id.btn_addLesson);
        edtLessonID = (EditText) view.findViewById(R.id.edt_lessonID);
        btn_Cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtLessonID.length()==0){
                    edtLessonID.requestFocus();
                    edtLessonID.setError("Vui lòng điền mã bài giảng");
                }else{
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String accessToken = preferences.getString("ACCESS_TOKEN",null);
                    AddLessonRequest addLessonRequest = new AddLessonRequest(Integer.parseInt(edtLessonID.getText().toString()));
                    ApiService.apiService.addLesson(addLessonRequest,"Bearer "+accessToken).enqueue(new Callback<AddLessonResponse>() {
                        @Override
                        public void onResponse(Call<AddLessonResponse> call, Response<AddLessonResponse> response) {
                            if(response.code()==200){
                                AddLessonResponse addLessonResponse = response.body();
                                String status = addLessonResponse.getStatus();
                                if (status.equals("lessonID is incorrect")){
                                    edtLessonID.requestFocus();
                                    edtLessonID.setError("Nhập sai mã bài giảng");
                                } else if (status.equals("lessonID is existed")){
                                    edtLessonID.requestFocus();
                                    edtLessonID.setError("Mã bài giảng đã có trong danh sách bài giảng");
                                } else if (status.equals("add successfully")){
                                    LessonListAcitivity lessonListAcitivity = (LessonListAcitivity) getContext();
                                    lessonListAcitivity.ThemBaiGiang(addLessonResponse.getLesson());
                                    getDialog().dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AddLessonResponse> call, Throwable t) {
                            Log.i("response","hihi");
                        }
                    });
                }
            }
        });
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
