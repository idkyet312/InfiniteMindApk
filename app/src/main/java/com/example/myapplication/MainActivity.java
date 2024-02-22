package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;

import android.app.Activity;
import android.content.Context;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import android.content.Context;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.io.InputStream;
import java.security.KeyStore;
import java.io.FileOutputStream;

import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 200;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 201;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private Button btnRecord;

    private KeyStoreHelper key;

    private TextView text1;
    private TextInputEditText editTextOne;
    private Context context;

    // Assuming KeyStoreHelper and CassandraConnector are correctly implemented elsewhere
    // private KeyStoreHelper keystoreHelper; // Not used directly in this snippet
    // private CassandraConnector cassandraConnector; // Assuming its instantiation requires context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Ensure the key pair is generated before signing

        btnRecord = findViewById(R.id.btnrecord);
        text1 = findViewById(R.id.textone);
        editTextOne = findViewById(R.id.myInputEditText);

        addKey();



        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/recording.3gp";

        btnRecord.setOnClickListener(v -> {
            if (checkPermissions()) {
                startRecording();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
            }
        });
    }
    public void addKey() {
        // Load the BKS keystore
        try {
            AssetManager asset = getAssets();
            System.out.println("add key");
            InputStream is = asset.open("mykeystore");
            System.out.println("add key1.5");
            KeyStore bksStore = KeyStore.getInstance("BKS");

            bksStore.load(is, "ubuntu".toCharArray());
            System.out.println(bksStore.getType());

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

        // Save the updated keystore to internal storage
            //key.saveKeystoreToFile(keyStore, getApplicationContext(), "updated_" + keystoreName, keystorePassword);


    private void startRecording() {
        if (TextUtils.isEmpty(editTextOne.getText())) {
            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                CassandraConnector connector = new CassandraConnector(this);
                File Dir = getFilesDir();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    connector.connect(Dir);
                }

            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                text1.setText("Recording failed to start.");
            }
        }
    }


    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                text1.setText("Permission denied. Cannot record.");
            }
        }
    }

    private class with {
    }
}
