package com.example.translator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.translator.databinding.ActivityMainBinding
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    //Binding 선언

    var option : FirebaseTranslatorOptions.Builder? = null
    //파이어베이스 번역기에 대한 옵션 설정 변수 선언

    var translater : FirebaseTranslator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        option = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.KO)
        //옵션 변수를 통해 번역할 언어 종류에 대해 설정 (영어, 한국어)

        translater = FirebaseNaturalLanguage.getInstance().getTranslator(option!!.build())
        translater?.downloadModelIfNeeded()
        //인스턴스 값(번역할 문장)을 받아 미리 설치한 모듈을 통해 번역을 실행
        //번역 설정 Apply 버튼 개념

        binding.inputText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                translater?.translate(s.toString())?.addOnSuccessListener {
                    binding.outText.text = it
                }//성공적으로 번역된 문장 데이터가 바인딩을 통해 outText에 표시
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }
}