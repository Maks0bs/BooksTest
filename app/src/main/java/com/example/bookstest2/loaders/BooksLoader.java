package com.example.bookstest2.loaders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;

import java.util.ArrayList;

public class BooksLoader extends AsyncTaskLoader<ArrayList<BooksVolume>> {
    private String mUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
    private int mStartIndex = 0;
    private String mQueryStr = null;
    private boolean mNewQuery = false;
    //private ProgressBar mProgressBar = null;
    //TODO keep track of amount of element in array list and fill up to the number, divisible by 10

    /*public BooksLoader(Context context, String url){
        super(context);
        mUrlStr = url;
    }*/

    public BooksLoader(Context context, String query, int startIndex){
        super(context);
        mQueryStr = query;
        mStartIndex = startIndex;
    }

    @Nullable
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<BooksVolume> loadInBackground() {

        mUrlStr = HTTPQueryUtils.BOOKS_API_START_STR + "v1/volumes?q=" + mQueryStr +
                "&startIndex=" + String.valueOf(mStartIndex);
        HTTPQueryUtils.BooksQueryManager booksQueryManager =
                new HTTPQueryUtils.BooksQueryManager(mUrlStr);
        ArrayList<BooksVolume> data = booksQueryManager.retrieveBooksList();
        return data;
    }
}
