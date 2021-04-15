package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonChoix1;
    private Button mButtonChoix2;
    private Button mButtonChoix3;
    private Button mButtonChoix4;
    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();

        mGame = new Game();
        updatingButtonsText(mGame.getPartie().getQuestions());
        Arrays.asList(mButtonChoix1, mButtonChoix2, mButtonChoix3, mButtonChoix4).forEach(button -> {
            button.setOnClickListener(this);
        });
    }

    private void initViews() {
        mButtonChoix1 = findViewById(R.id.btn_choix1);
        mButtonChoix2 = findViewById(R.id.btn_choix2);
        mButtonChoix3 = findViewById(R.id.btn_choix3);
        mButtonChoix4 = findViewById(R.id.btn_choix4);
    }

    private void updatingButtonsText(List<Country> questions) {
        AtomicInteger integer = new AtomicInteger(0);
        Arrays.asList(mButtonChoix1, mButtonChoix2, mButtonChoix3, mButtonChoix4).forEach(button -> {
            int i = integer.get();
            Country question = questions.get(i);
            button.setText(question.getName());
            button.setTag(question);
            integer.getAndIncrement();
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choix1:
            case R.id.btn_choix2:
            case R.id.btn_choix3:
            case R.id.btn_choix4:
                Country country = (Country) v.getTag();
                Log.d("TAG", "" + country.getId() + mGame.getPartie().getResponse().getId());
                if(country.getId() == mGame.getPartie().getResponse().getId()) {
                    Toast.makeText(this, "Congrats !!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Wrong answer ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
//        Log.d("TAG", v.getTag().toString());
//
    }
}


class Country {
    private int id;
    private String name;
    private int flag;

    public Country(int id, String name, int urlFlag) {
        this.id = id;
        this.name = name;
        this.flag = urlFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", flag=" + flag +
                '}';
    }
}

class Game {

    Game() {
        newQuestion();
    }
    private Partie partie;
    private ArrayList<Country> data = new ArrayList<Country>() {{
        add(new Country(1, "Algerie", R.drawable.dice_1));
        add(new Country(2, "Madagascar", R.drawable.dice_2));
        add(new Country(3, "Vénézuela", R.drawable.dice_3));
        add(new Country(4, "Cameroun", R.drawable.dice_4));
        add(new Country(5, "Guinée", R.drawable.dice_5));
        add(new Country(6, "Maroc", R.drawable.dice_6));
    }};
    private List<Integer> indexes = IntStream.range(0, data.size() - 1).boxed().collect(Collectors.toList());


    public Partie newQuestion() {
        Collections.shuffle(indexes);
        List<Country> questions = indexes.stream().limit(4).map(index -> data.get(index)).collect(Collectors.toList());
        Random r = new Random();
        Country response = questions.get(r.nextInt(questions.size()));
        Partie partie = new Partie(questions, response);
        setPartie(partie);
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    public Partie getPartie() {
        return partie;
    }
}

class Partie {
    private List<Country> questions;
    private Country response;

    public Partie(List<Country> questions, Country response) {
        this.questions = questions;
        this.response = response;
    }

    public List<Country> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Country> questions) {
        this.questions = questions;
    }

    public Country getResponse() {
        return response;
    }

    public void setResponse(Country response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "Partie{" +
                "questions=" + questions +
                ", response=" + response +
                '}';
    }
}