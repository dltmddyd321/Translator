package com.example.translater_version1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdit;
    private ImageView mic;
    private MaterialButton translateBtn;
    private TextView translateResult;

    String[] fromLanguages = {"From","English","Japan","Korea"};
    //번역 대상 스피너에 담길 요소를 배열로 선언

    String[] toLanguages = {"To","English","Japan","Korea"};
    //번역 결과 스피너에 담길 요소를 배열로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromSpinner = findViewById(R.id.startSpinner);
        toSpinner = findViewById(R.id.resultSpinner);
        sourceEdit = findViewById(R.id.editSource);
        mic = findViewById(R.id.mic);
        translateBtn = findViewById(R.id.translateBtn);
        translateResult = findViewById(R.id.translateResult);
    }
}