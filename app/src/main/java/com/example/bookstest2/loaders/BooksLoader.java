package com.example.bookstest2.loaders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

//import com.example.bookstest2.fragments.BooksFragmentRecycler;
import com.example.bookstest2.fragments.BooksFragmentRecycler;
import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;

import java.util.ArrayList;

public class BooksLoader extends AsyncTaskLoader<ArrayList<BooksVolume>> {
    //private String mUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
    private int mStartIndex = 0;
    private String mQueryStr = null;
    private boolean mNewQuery = false;
    private boolean mActive = false;
    private BooksFragmentRecycler mFragment = null;//TODO this is very inefficient in terms of memory!!!
    //private ProgressBar mProgressBar = null;

    /*public BooksLoader(Context context, String url){
        super(context);
        mUrlStr = url;
    }*/

    public BooksLoader(Context context, String query, int startIndex, BooksFragmentRecycler fragment){
        super(context);
        mQueryStr = query;
        mStartIndex = startIndex;
        mFragment = fragment;
    }

    @Nullable
    @Override
    protected void onStartLoading() {
        if (mActive){
            return;
        }
        Log.e("BooksLoader", "onstartloading called");
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<BooksVolume> loadInBackground() {
        mActive = true;

        Log.e("LOADINBACKGROUND", "started loading");

        String curUrl = HTTPQueryUtils.BOOKS_API_START_STR + "v1/volumes?q=" + mQueryStr +
                "&maxResults=20" +
                "&startIndex=" + String.valueOf(mStartIndex);
        HTTPQueryUtils.BooksQueryManager booksQueryManager =
                new HTTPQueryUtils.BooksQueryManager(curUrl);

        ArrayList<BooksVolume> response = booksQueryManager.retrieveBooksList();
        ArrayList<BooksVolume> data = new ArrayList<>();

        if (response == null || response.size() == 0){
            return data;
        }

        for (int i = 0; i < response.size(); i++){
            BooksVolume curBook = response.get(i);
            //BooksFragmentRecycler.updateLoadingPosition(1);
            mFragment.updateLoadingPosition(1);
            if (!curBook.getTitle().equals("TEST") && !curBook.getAuthor().equals("TEST")){
                data.add(curBook);
            }

            if (data.size() >= 10){
                break;
            }
        }

        return data;
    }
}
