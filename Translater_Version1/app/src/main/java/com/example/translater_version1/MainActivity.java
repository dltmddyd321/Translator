package com.example.translater_version1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdit;
    private ImageView mic;
    private MaterialButton translateBtn;
    private TextView translateResult;

    String[] fromLanguages = {"From","English","Japanese","Korean"};
    //번역 대상 스피너에 담길 요소를 배열로 선언

    String[] toLanguages = {"To","English","Japanese","Korean"};
    //번역 결과 스피너에 담길 요소를 배열로 선언

    private static final int REQUEST_PERMISSION_CODE = 1;
    //권한 허가에 대한 key code 선언

    int languageCode, fromLanguageCode, toLanguageCode = 0;
    //언어 입력 데이터에 대한 key code 선언

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

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //번역 대상 스피너의 요소가 선택될 경우
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
                //번역 대상 스피너 지정 값에 알맞은 형태로 언어 표현
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

       ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.item_spinner,fromLanguages);
       //스피너에 번역 대상 언어의 목록을 생성하기 위한 Adapter 생성

       fromAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
       //안드로이드에서 제공하는 기본 폼으로 드롭 다운 뷰를 설정 (추후 커스터마이징 가능)

       fromSpinner.setAdapter(fromAdapter);
       //번역 대상 스피너에 Adapter 연결

       toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           //번역 결과과 스피너의 소가 선택될 경우
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               toLanguageCode = getLanguageCode(toLanguages[position]);
               //번역 결과 스피너 지정 값에 알맞은 형태로 언어 표현
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) { }
       });

       ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.item_spinner,toLanguages);
       //스피너에 번역 결과 언어의 목록을 생성하기 위한 Adapter 생성

       toAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
       //안드로이드에서 제공하는 기본 폼으로 드롭 다운 뷰를 설정 (추후 커스터마이징 가능)

       toSpinner.setAdapter(toAdapter);
       //번역 결과 스피너에 Adapter 연결

       translateBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               translateResult.setText("");
               if(sourceEdit.getText().toString().isEmpty()) {
                   //번역할 내용이 입력되지 않았다면 메시지 출력
                   Toast.makeText(MainActivity.this,"번역할 내용을 입력해주세요!",Toast.LENGTH_SHORT).show();
               } else if(fromLanguageCode == 0) {
                   //번역 대상 언어가 지정되지 않았다면 메시지 출력
                   Toast.makeText(MainActivity.this,"번역 대상 언어를 선택해주세요!",Toast.LENGTH_SHORT).show();
               } else if(toLanguageCode == 0) {
                   //번역 결과 언어가 지정되지 않았다면 메시지 출력
                   Toast.makeText(MainActivity.this,"번역 결과 언어를 선택해주세요!",Toast.LENGTH_SHORT).show();
               } else {
                   //번역할 내용이 입력되어있고, 대상 언어와 결과 언어가 지정되었다면 그들을 인수로 받아 번역 함수 실행
                   translateText(fromLanguageCode, toLanguageCode, sourceEdit.getText().toString());
               }
           }
       });

       mic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
               //음성 인식을 통해 데이터를 지원

               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
               //자유형 음성 인식을 기반으로 수행 시 어떤 형태의 음성 모델을 선호하는 인식자에게 알리기

               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
               //지정된 언어 항목에 대한 Locale 현재 값을 가져오기

               intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"입력될 내용을 말씀하세요!");
               //사용자에게 말하도록 요청할 때 선택적 텍스트 프롬프트를 출력

               try {
                   //입력 결과 출력에 대한 오류를 try ~ catch 구문으로 예외 처리
                   startActivityForResult(intent, REQUEST_PERMISSION_CODE);
               } catch (Exception e) {
                   e.printStackTrace();
                   Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //MainActivity에서의 결과 로직 처리
        if(requestCode == REQUEST_PERMISSION_CODE) {
            //권한 허가 완료이며
            if(resultCode == RESULT_OK && data != null) {
                //결과 출력이 완료되고, 데이터가 비어있지 않다면
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //음성인식을 통해 가져온 데이터의 문자열 값을 result 배열에 저장

                sourceEdit.setText(result.get(0));
                //음성인식을 통해 가져온 문자열 데이터가 번역할 내용에 반영
            }
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        //번역 대상 언어, 번역 결과 언어, 번역할 문자열을 인자 값으로 받아와 실제 번역을 진행할 함수 생성
        //번역을 진행하는 객체 Options를 생성하여 대상 언어와 결과 언어를 받아 빌드
        translateResult.setText("Downloading Model...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        //자연적 번역을 위한 머신 러닝 기반 Entry class로서, 인스턴스로 options 객체를 요구

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        //번역을 위한 파이어베이스 모델을 지원받기

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateResult.setText("번역 진행 중...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translateResult.setText(s);
                        //번역에 성공하면 성공한 결과 내용이 결과창에 출력
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        //번역 과정 실패 시 예외 처리
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                //파이어베이스 모델 호출 과정 실패 시 예외 처리
            }
        });
    }

    public int getLanguageCode(String language) {
        int languageCode = 0;
        //파이어베이스 번역 도구를 위한 코드 선언
        switch (language) {
            case "English":
                //스피너의 English가 선택되면 영어로 번역
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Japanese":
                //스피너의 Japanese가 선택되면 일본어로 번역
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Korean":
                //스피너의 Korean이 선택되면 한국어로 번역
                languageCode = FirebaseTranslateLanguage.KO;
                break;
        }
        return languageCode;
        //번역 결과(코드 값) 반환
    }
}