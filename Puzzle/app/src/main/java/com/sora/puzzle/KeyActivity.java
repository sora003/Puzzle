package com.sora.puzzle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sora.puzzle.View.Answer;

/**
 * Created by Sora on 2016/1/21.
 */
public class KeyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Answer(this));
    }
}
