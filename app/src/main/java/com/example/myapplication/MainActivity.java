package com.example.myapplication;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
// Other imports as needed



public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private TextView textViewUser;
    public TextView InputText;

    private TextToSpeech textToSpeech;

    private ActivityResultLauncher<Intent> speechResultLauncher;

    public boolean AutoSpeak = false;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;

    private static final int REQUEST_MICROPHONE = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private ListView listView;
    private Button speechButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check for RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
        } else {
            initializeSpeechRecognizer();
        }


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());


        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        textViewUser = findViewById(R.id.AIInput);
        textViewUser.setMovementMethod(new ScrollingMovementMethod()); // Enable scrolling


        checkSpeechRecognizer();




        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US); // Set your desired language
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        setup();
                        // Your code to execute when the TTS is initialized
                    }
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });
        // Initialize your Retrofit service
    }



    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        speechRecognizer.setRecognitionListener(listener);
    }
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkSpeakingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!textToSpeech.isSpeaking()) {
                AutoSpeak = false;
                ResetMic();
                promptSpeechInput(null);
            } else if (textToSpeech.isSpeaking()) {
                // Re-post the Runnable to check again after 2 seconds
                handler.postDelayed(this, 100);
            }
        }
    };


    private void speakOut(String text) {
        textToSpeech.stop();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        // Start checking if TextToSpeech is still speaking after initiating speech
        handler.postDelayed(checkSpeakingRunnable, 1);
    }

    public void setup()
    {
        speakOut("Welcome to Infinite Mind Choose a weapon to begin: Speak now");
    }

    public void ResetMic()
    {

        initializeSpeechRecognizer();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            textToSpeech.stop();
            ResetMic();
            speakOut("I didnt hear you:");
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(matches.get(0) != null) {
                matches.set(0, matches.get(0).replace("I didnt hear you", ""));
                OutputCode(matches.get(0));
                speechRecognizer.destroy();
                mSpeechRecognizer.destroy();
                results = null;
                //textViewUser.setText(matches.get(0));
            }
            matches.set(0, null);
            //mSpeechRecognizer.stopListening();
            //Toast.makeText(null, "Permission Denied", Toast.LENGTH_SHORT).show();
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MICROPHONE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSpeechRecognizer();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
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
        speechRecognizer.destroy();
        mSpeechRecognizer.destroy();
        textToSpeech.stop();
        AutoSpeak = true;
        ResetMic();
        speechRecognizer.startListening(speechRecognizerIntent);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
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
                    speechRecognizer.destroy();
                    mSpeechRecognizer.destroy();
                    Data data = response.body();
                    String textdone = textViewUser.getText().toString() + "\n\nYou: " + finalText + "\n" + data.toString() + "\n";
                    speakOut(data.toString() + " Speak now");
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
