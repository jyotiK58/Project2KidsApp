package com.learningapp;

import android.os.AsyncTask;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadDrawingTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String imageData = strings[0];

        try {
            URL url = new URL("http://10.0.2.2/PhpForKidsLearninApp/insert_drawing.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write("image=" + imageData);
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "Upload successful";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Upload failed";
    }
}
