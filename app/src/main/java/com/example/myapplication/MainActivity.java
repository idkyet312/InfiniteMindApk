package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import org.w3c.dom.Text;
// Other imports as needed



public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private TextView textViewUser;
    public TextView InputText;

    private TextToSpeech textToSpeech;

    private ActivityResultLauncher<Intent> speechResultLauncher;

    public boolean AutoSpeak = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewUser = findViewById(R.id.AIInput);
        textViewUser.setMovementMethod(new ScrollingMovementMethod()); // Enable scrolling

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        checkSpeechRecognizer();



        speechResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            String spokenText = matches.get(0);
                            OutputCode(spokenText);
                        }
                    }
                }
        );
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US); // Set your desired language
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        // Your code to execute when the TTS is initialized
                    }
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });

        // Initialize your Retrofit service
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    private void checkSpeechRecognizer() {
        // Check if speech recognition is supported
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            // Disable or hide your speech to text UI components
            Log.e("STT", "Speech recognition is not supported on this device.");
        }
    }



    final int REQUEST_CODE_SPEECH_INPUT = 100;


    public void promptSpeechInput(View view) {
        AutoSpeak = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        Intent SpeechInput = intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");

        try {
            speechResultLauncher.launch(intent);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech not supported", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void EnterInput(View view)
    {
        OutputCode(null);
    }

    public void OutputCode(String SpeachInput){
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        InputText = findViewById(R.id.UserInput);

        String text = String.valueOf(InputText.getText());

        InputText.setText("");

        if(AutoSpeak)
        {
            text = SpeachInput;
        }

        Data datasend = new Data(text, "");


        Call<Data> callToAi = apiService.getUsers(datasend);
        String finalText = text;
        callToAi.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {
                    Data data = response.body();
                    String textdone = textViewUser.getText().toString() + "\n\nYou: " + finalText + "\n" + data.toString();
                    speakOut(data.toString());
                    textViewUser.setText(textdone);


                    // Scroll to the bottom
                    final int scrollAmount = textViewUser.getLayout().getLineTop(textViewUser.getLineCount()) - textViewUser.getHeight();
                    if (scrollAmount > 0)
                        textViewUser.scrollTo(0, scrollAmount);
                    else
                        textViewUser.scrollTo(0, 0);
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
