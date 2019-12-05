package com.example.bookstest2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class HTTPQueryUtils {
    public static final String BOOKS_API_START_STR = " https://www.googleapis.com/books/";

    public static class BooksQueryManager {
        private String mQueryUrlStr;

        public BooksQueryManager(String queryUrlStr){
            mQueryUrlStr = queryUrlStr;
        }

        public ArrayList<BooksVolume> retrieveBooksList(){//may not need to be static
            URL url = createUrlFromStr(mQueryUrlStr);
            if (url == null){
                //Print error
                return null;
            }


            String jsonResponse = null;


            try {
                jsonResponse = readStringFromStream(openHttpConnection(url));
            }
            catch (IOException e) {
                 Log.e("RETRIEVEJSON", "Problem making the HTTP request.", e);
            }

             if (TextUtils.isEmpty(jsonResponse)){
                 Log.e("JSONRESPONSE", "is empty");
                 return null;
             }
             return getFeaturesFromJson(jsonResponse);
        }

        private static BooksVolume getOneFeatureFromJson(JSONObject input){
            try {
                JSONObject curVolumeInfo = input.getJSONObject("volumeInfo");

                //getting title
                String curTitle = curVolumeInfo.getString("title");

                //getting authors
                JSONArray curAuthors = curVolumeInfo.getJSONArray("authors");
                ArrayList<String> curAuthorsList = new ArrayList<>();
                String curAuthorsStr = ""; //TODO may need to change to smth else in the future
                for (int j = 0; j < curAuthors.length(); j++) {
                    String curAuthor = curAuthors.getString(j);
                    curAuthorsList.add(curAuthor);
                    if (j == 0) {
                        curAuthorsStr = curAuthorsStr + curAuthor;
                    } else {
                        curAuthorsStr = curAuthorsStr + ", " + curAuthor;
                    }
                }

                JSONObject curImageLinks = curVolumeInfo.getJSONObject("imageLinks");
                String curThumbnailUrlStr = curImageLinks.getString("thumbnail");
                //TODO may need to work with possible null url
                Bitmap curThumbnail = downloadImage(createUrlFromStr(curThumbnailUrlStr));

                double curRating = BooksVolume.NO_RATING_PROVIDED;
                if (curVolumeInfo.has("averageRating")) {
                    curRating = curVolumeInfo.getDouble("averageRating");
                }

                JSONObject curSaleInfo = input.getJSONObject("saleInfo");

                boolean curIsEBook = curSaleInfo.getBoolean("isEbook");

                String curPrice = BooksVolume.NO_PRICE_PROVIDED;

                if (curSaleInfo.getString("saleability").equals("FOR_SALE")) {
                    JSONObject curListPrice = curSaleInfo.getJSONObject("listPrice");
                    curPrice = String.valueOf(curListPrice.getDouble("amount"));
                    curPrice = curPrice + " " + curListPrice.getString("currencyCode");
                }

                return new BooksVolume(curTitle, curAuthorsStr, curIsEBook, curPrice, curRating, curThumbnail);
            }
            catch(JSONException e){
                Log.e("JSONPARSING OF ONE", "failed");
                return new BooksVolume("TEST", "TEST", true, "5.99 EUR", 5.0);
            }
        }

        private static ArrayList<BooksVolume> getFeaturesFromJson(String inputJSON){
            //TODO may need to change parses for various responses/items
            if (TextUtils.isEmpty(inputJSON)){
                return null;
            }

            ArrayList<BooksVolume> books = new ArrayList<>();

            try{
                JSONObject root = new JSONObject(inputJSON);
                JSONArray items = root.getJSONArray("items");
                ArrayList<BooksVolume> curBooks = new ArrayList<>();
                for (int i = 0; i < items.length(); i++){
                    JSONObject curItem = items.getJSONObject(i);
                    //TODO this is a temporary fix, may need to make it load 10 items in any case
                    BooksVolume newBook = getOneFeatureFromJson(curItem);
                    if (newBook != null){
                        curBooks.add(newBook);
                        Log.e("JSONPARSING ADD ONE", "ok");
                    }
                }

                books = curBooks;
            }
            catch(JSONException e){
                Log.e("JSONPARSING", "failed");
            }
            return books;
        }
    }

    private static URL createUrlFromStr(String urlStr) {
        URL url = null;

        try{
            url = new URL(urlStr);
        }
        catch (MalformedURLException e){
            Log.e("HTTPQUERY", "Problem building the URL ", e);
        }

        return url;
    }

    private static String readStringFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static Bitmap downloadImage(URL url) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = openHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

        return bitmap;
    }

    private static InputStream openHttpConnection(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return inputStream;
    }

}
