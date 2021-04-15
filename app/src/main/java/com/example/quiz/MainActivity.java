package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    private Button mButtonJouer;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mButtonJouer.setOnClickListener(View -> {
            //lance l'acitity Game
            startActivity(new Intent(this, GameActivity.class));
        });
    }

    private  void initViews () {
        mButtonJouer = findViewById(R.id.btn_jouer);
    }
}
