package com.example.myapplication;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.io.FileOutputStream;

import java.security.cert.Certificate;
import java.util.Enumeration;

public class KeyStoreHelper {

        public void loadKeystoreFromAssets(@NonNull Context context) {
            try {
                // Load the keystore - replace `password` with the actual password
                char[] password = "ubuntu".toCharArray();
                KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    try(InputStream is = Files.newInputStream(Paths.get("mykeystore.jks"))) {
                        keystore.load(is, password);
                    }
                }

                // Enumerate through the aliases
                Enumeration<String> enumeration = keystore.aliases();
                while(enumeration.hasMoreElements()) {
                    String alias = enumeration.nextElement();
                    System.out.println("Alias: " + alias);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    public void addKeyToKeystore(KeyStore keyStore, String alias, PrivateKey privateKey, Certificate[] certChain, String keyPassword) throws Exception {
        keyStore.setKeyEntry(alias, privateKey, keyPassword.toCharArray(), certChain);
    }


    public void saveKeystoreToFile(KeyStore keyStore, Context context, String fileName, String keystorePassword) throws Exception {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            keyStore.store(fos, keystorePassword.toCharArray());
        }
    }
}
