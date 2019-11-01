package com.example.bookstest2;

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
import java.util.Arrays;
import java.util.List;

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
                jsonResponse = HTTPRequestResponseStr(url);
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
                Log.e("JSONPARSINGINFO", "root");
                for (int i = 0; i < items.length(); i++){
                    JSONObject curItem = items.getJSONObject(i);
                    JSONObject curVolumeInfo = curItem.getJSONObject("volumeInfo");

                    Log.e("JSONPARSINGINFO", "volumeInfo");
                    //getting title
                    String curTitle = curVolumeInfo.getString("title");

                    Log.e("JSONPARSINGINFO", "volumeInfo title");

                    //getting authors
                    JSONArray curAuthors = curVolumeInfo.getJSONArray("authors");
                    ArrayList<String> curAuthorsList = new ArrayList<>();
                    Log.e("JSONPARSINGINFO", "authors 1");
                    String curAuthorsStr = ""; //may need to change to smth else in the future
                    for (int j = 0; j < curAuthors.length(); j++){
                        String curAuthor = curAuthors.getString(j);
                        curAuthorsList.add(curAuthor);
                        if (j == 0){
                            curAuthorsStr = curAuthorsStr + curAuthor;
                        }
                        else{
                            curAuthorsStr = ", " + curAuthor;
                        }
                    }

                    Log.e("JSONPARSINGINFO", "authors on volumeinfo");

                    JSONObject curImageLinks = curVolumeInfo.getJSONObject("imageLinks");
                    String curThumbnailUrlStr = curImageLinks.getString("thumbnail");

                    Log.e("JSONPARSINGINFO", "imageinfo");

                    double curRating = BooksVolume.NO_RATING_PROVIDED;
                    if (curVolumeInfo.has("averageRating")){
                        curRating = curVolumeInfo.getDouble("averageRating");
                    }

                    Log.e("JSONPARSINGINFO", "ratin info");

                    JSONObject curSaleInfo = curItem.getJSONObject("saleInfo");

                    Log.e("JSONPARSINGINFO", "saleinfo 1");

                    boolean curIsEBook = curSaleInfo.getBoolean("isEbook");

                    String curPrice = BooksVolume.NO_PRICE_PROVIDED;

                    if (curSaleInfo.getString("saleability") == "FOR_SALE"){
                        JSONObject curListPrice = curSaleInfo.getJSONObject("listPrice");
                        curPrice = String.valueOf(curListPrice.getDouble("amount"));
                        curPrice = curPrice + " " + curListPrice.getString("currencyCode");
                    }

                    Log.e("JSONPARSINGINFO", "saleinfo price");

                    curBooks.add(
                            new BooksVolume(curTitle, curAuthorsStr, curIsEBook, curPrice, curRating, curThumbnailUrlStr)
                    );

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

    private static String HTTPRequestResponseStr(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("HTTPREQUEST", "Error response code: " + urlConnection.getResponseCode());
            }

        }
        catch (IOException e){
            Log.e("HTTPREQUEST", "Problem retrieving JSON results.", e);
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close(); //impossible exception
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
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
}
