package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.datastax.oss.driver.api.core.CqlSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

    // Your existing methods...



    public class CassandraConnector {
        private final Context context;

        public CassandraConnector(Context context) {
            this.context = context;
        }

        public void copySecureConnectBundleToInternalStorage() {
            AssetManager assetManager = context.getAssets(); // Access AssetManager
            String filename = "secureconnect2.zip";
            InputStream in = null;
            OutputStream out = null;

            try {
                in = assetManager.open("secureconnect.zip"); // Open an input stream to the asset
                File outFile = new File(context.getFilesDir(), filename); // Define output file path

                out = new FileOutputStream(outFile);
                copyFile(in, out); // Copy the file
                System.out.println("File copied to " + outFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace(); // Handle errors
            } finally {
                // Ensure streams are closed, even if an exception is thrown
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void connect(Object dir) {
        // Ensure the secure connect bundle has been copied to internal storage first

        copySecureConnectBundleToInternalStorage();


        String filename = "secureconnect2.zip"; // The file name
        File outFile = new File("/data/user/0/com.example.myapplication/files/secureconnect2.zip"); // Path in internal storage

        try {
            session = CqlSession.builder()
                    .withCloudSecureConnectBundle(outFile.toPath())
                    .withAuthCredentials("username", "password") // Replace with actual credentials
                    .build();
            System.out.println("Connected to Cassandra");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle connection errors
        }
    }


    // Other methods remain unchanged...

    public void close() {
        if (session != null) {
            session.close();
            System.out.println("Disconnected from Cassandra");
        }
    }

    public void insertChatHistory(String user123, String s) {
            int a = 1;
    }

        private CqlSession session;
    }
