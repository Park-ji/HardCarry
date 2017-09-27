package com.project.safereturn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private Switch aSwitchShake;
    private Switch aSwitchLock;
    private Switch aSwitchShouting;

    private EditText editTextPhone01;
    private EditText editTextPhone02;
    private EditText editTextPhone03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        aSwitchShake = (Switch) findViewById(R.id.switch_shake);
        aSwitchLock = (Switch) findViewById(R.id.switch_lock);
        aSwitchShouting = (Switch) findViewById(R.id.switch_shouting);

        editTextPhone01 = (EditText) findViewById(R.id.editText_phone01);
        editTextPhone02 = (EditText) findViewById(R.id.editText_phone02);
        editTextPhone03 = (EditText) findViewById(R.id.editText_phone03);

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyApplication.editor.putBoolean("shake", aSwitchShake.isChecked());
                MyApplication.editor.putBoolean("lock", aSwitchLock.isChecked());
                MyApplication.editor.putBoolean("shouting", aSwitchShouting.isChecked());

                MyApplication.editor.putString("phone01", editTextPhone01.getText().toString());
                MyApplication.editor.putString("phone02", editTextPhone02.getText().toString());
                MyApplication.editor.putString("phone03", editTextPhone03.getText().toString());

                MyApplication.editor.commit();
                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
