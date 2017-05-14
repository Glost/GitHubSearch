/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents the methods for making requests by the given URL string.
 */
public class Request {

    /**
     * Makes the "GET" request by the given URL string and returns the string response.
     * @param urlString The given URL string.
     * @return The string response.
     * @throws IOException If some connection problem happened (including exceeding GitHub's API limits for the amount of consequent requests).
     */
    public static String makeStringRequest(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("GET");

        String response = "";

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String nextLine;
        while ((nextLine = bufferedReader.readLine()) != null) {
            response += nextLine;
        }
        bufferedReader.close();

        return response;
    }

    /**
     * Loads the image from the server by the given URL string.
     * @param urlString The given URL string.
     * @return The loaded image.
     * @throws IOException If some connection problem happened (including exceeding GitHub's API limits for the amount of consequent requests).
     */
    public static Bitmap makeBitmapRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}
