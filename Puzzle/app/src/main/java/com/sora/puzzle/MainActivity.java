package com.sora.puzzle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sora.puzzle.View.Question;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Question(this));

    }


}
