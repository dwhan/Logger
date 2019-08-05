package com.raonsecure.rslsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class UIEventTestActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uievent_test);

        Button button = (Button) findViewById(R.id.button);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        RadioButton radioButton = (RadioButton) findViewById(R.id.radioButton);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        button.setOnClickListener(this::onClick);
        imageButton.setOnClickListener(this::onClick);
        checkBox.setOnClickListener(this::onClick);
        radioButton.setOnClickListener(this::onClick);
        toggleButton.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {

    }
}
