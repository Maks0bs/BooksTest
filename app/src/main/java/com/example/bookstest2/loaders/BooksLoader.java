package com.example.bookstest2.loaders;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookstest2.BooksVolume;
import com.example.bookstest2.HTTPQueryUtils;

import java.util.ArrayList;
import java.util.List;

public class BooksLoader extends AsyncTaskLoader<ArrayList<BooksVolume>> {
    private String mUrlStr;

    public BooksLoader(Context context, String url){
        super(context);
        mUrlStr = url;
    }

    @Nullable
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<BooksVolume> loadInBackground() {

        HTTPQueryUtils.BooksQueryManager booksQueryManager =
                new HTTPQueryUtils.BooksQueryManager(mUrlStr);

        return booksQueryManager.retrieveBooksList();
    }
}
