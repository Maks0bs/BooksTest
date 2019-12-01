package com.example.bookstest2.loaders;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;

import java.util.ArrayList;

public class BooksLoader extends AsyncTaskLoader<ArrayList<BooksVolume>> {
    private String mUrlStr;
    private ProgressBar mProgressBar = null;

    public BooksLoader(Context context, String url, ProgressBar progressBar){
        super(context);
        mUrlStr = url;
        mProgressBar = progressBar;
    }

    public BooksLoader(Context context, String url){
        super(context);
        mUrlStr = url;
    }

    @Nullable
    @Override
    protected void onStartLoading() {
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<BooksVolume> loadInBackground() {

        HTTPQueryUtils.BooksQueryManager booksQueryManager =
                new HTTPQueryUtils.BooksQueryManager(mUrlStr);

        ArrayList<BooksVolume> data = booksQueryManager.retrieveBooksList();
        return data;
    }
}
