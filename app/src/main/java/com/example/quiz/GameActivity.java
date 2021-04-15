package com.example.quiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.sharp.Sharp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private Button mButtonChoix1;
    private Button mButtonChoix2;
    private Button mButtonChoix3;
    private Button mButtonChoix4;
    private TextView mTextViewScore;
    private ImageView mImageView;
    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();

        mGame = new Game(this);
        updateUI();
        Arrays.asList(mButtonChoix1, mButtonChoix2, mButtonChoix3, mButtonChoix4).forEach(button -> {
            button.setOnClickListener(this);
        });
    }

    /**
     * Initialise les buttons, l'image view et le text view
     */
    private void initViews() {
        mButtonChoix1 = findViewById(R.id.btn_choix1);
        mButtonChoix2 = findViewById(R.id.btn_choix2);
        mButtonChoix3 = findViewById(R.id.btn_choix3);
        mButtonChoix4 = findViewById(R.id.btn_choix4);
        mImageView = findViewById(R.id.image_flag);
        mTextViewScore = findViewById(R.id.textViewScore);
    }

    private void updateUI() {
        Partie partie = mGame.getPartie();
        List<Country> questions = partie.getQuestions();
        Country response = partie.getResponse();
        AtomicInteger integer = new AtomicInteger(0);
        Arrays.asList(mButtonChoix1, mButtonChoix2, mButtonChoix3, mButtonChoix4).forEach(button -> {
            int i = integer.get();
            Country question = questions.get(i);
            button.setText(question.getName());
            button.setTag(question);
            integer.getAndIncrement();
        });
        Utils.fetchSvg(this, response.getFlag(), mImageView);
    }

    /**
     * Nouvelle question
     */
    public void newPartie() {
        mGame.newQuestion();
        updateUI();
    }

    /**
     * Met à jour le textview du score
     */
    public void updateScoreView() {
        String score = "Score : " + mGame.getScore();
        mTextViewScore.setText(score);
    }


    /**
     * Listener de tous des boutons, chaque bouton est associé à un pays,
     * cette associations est faite ent utilsant button.setTag dans la méthode
     * updateUI (button.setTag(question))
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choix1:
            case R.id.btn_choix2:
            case R.id.btn_choix3:
            case R.id.btn_choix4:
                Country country = (Country) v.getTag();

                if (country.getName().equals(mGame.getPartie().getResponse().getName())) {
                    Toast.makeText(this, "Congrats !!", Toast.LENGTH_SHORT).show();
                    mGame.correctAnswer();
                    newPartie();
                } else {
                    mGame.wrongAnswer();
                    Toast.makeText(this, "Wrong answer ", Toast.LENGTH_SHORT).show();
                }
                updateScoreView();
                break;
        }
    }
}


class Country {
    private String name;
    private String flag;

    public Country(String name, String flag) {
        this.name = name;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Country{" +
                ", name='" + name + '\'' +
                ", flag=" + flag +
                '}';
    }
}

class Game {
    private Context mContext;
    private List<Country> countries;
    private List<Integer> indexes;
    private final int BASE_SCORE = 3;
    private int score;
    private int attempt;

    /**
     * Constructeur Game et initialise le jeu
     * @param context
     */
    Game(Context context) {
        this.mContext = context;
        this.countries = loadCountries();
        this.indexes = IntStream.range(0, this.countries.size() - 1).boxed().collect(Collectors.toList());
        this.score = 0;
        newQuestion();
    }

    private Partie partie;

    /**
     * Lit le fichier json en appelant la méthode définie dans la classe Utils et
     * mape chaque pays à un objet Country
     * @return
     */
    private List<Country> loadCountries() {
        Optional<String> jsonFileString = Utils.getJsonFromAssets(mContext, "countries.json");
        Gson gson = new Gson();
        Type listCountryType = new TypeToken<List<Country>>() {
        }.getType();
        return gson.fromJson(jsonFileString.orElse(""), listCountryType);
    }

    /**
     * Genère une nouvelle questions en melangeant (shuffle) à chaque fois
     * les indexes des pays stocké dans `indexes` et la réponse associée.
     * @return
     */
    public Partie newQuestion() {
        Collections.shuffle(indexes);
        List<Country> questions = indexes.stream().limit(4).map(index -> countries.get(index)).collect(Collectors.toList());
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public int getAttempt() {
        return attempt;
    }

    public void wrongAnswer() {
        if (this.score > 0) {
            this.setScore(this.score - 1);
        }
        this.setAttempt(++attempt);
    }

    public void correctAnswer() {
        this.setScore(score + BASE_SCORE - attempt);
        this.setAttempt(0);
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

class Utils {

    /**
     * Recupère le contenu du fichier json
     * qu'on va parser par la suite en utilisant la librairie Gson
     * @param context
     * @param fileName
     * @return
     */
    static Optional<String> getJsonFromAssets(Context context, String fileName) {
        String jsonString = null;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(jsonString);
    }

    private static OkHttpClient httpClient;

    // this method is used to fetch svg and load it into target imageview.
    public static void fetchSvg(Context context, String url, final ImageView target) {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .cache(new Cache(context.getCacheDir(), 5 * 1024 * 1014))
                    .build();
        }

        // here we are making HTTP call to fetch data from URL.
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // we are adding a default image if we gets any error.
                target.setImageResource(R.drawable.dice_1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // sharp is a library which will load stream which we generated
                // from url in our target imageview.
                InputStream stream = response.body().byteStream();
                Sharp.loadInputStream(stream).into(target);
                stream.close();
            }
        });
    }
}