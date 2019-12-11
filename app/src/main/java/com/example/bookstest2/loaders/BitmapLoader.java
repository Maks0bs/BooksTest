package com.example.bookstest2.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookstest2.fragments.BooksFragmentRecycler;
import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;

import java.util.ArrayList;

//TODO type may have to ArrayList of bitmaps, but i'm not sure
public class BitmapLoader extends AsyncTaskLoader<ArrayList<Bitmap>> {
    private ArrayList<String> mUrls = null;
    private Fragment mFragment = null;//TODO this is very inefficient in terms of memory!!!
    private HTTPQueryUtils.HTTPQueryUtilsPublicManager mHTTPPublicManager =
            new HTTPQueryUtils.HTTPQueryUtilsPublicManager();

    public BitmapLoader (Context context, ArrayList<String> urls, Fragment fragment){
        super(context);
        mUrls = urls;
        mFragment = fragment;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Bitmap> loadInBackground() {
        ArrayList<Bitmap> result = new ArrayList<Bitmap>();

        while(!mUrls.isEmpty()){
            /*if (.equals("DISCONNECTED")){
                return;
            }*/
            Log.e("BITMAPLOADER", "trying to load image");



            Bitmap bitmap = mHTTPPublicManager.downloadImage(mUrls.get(0));
            if (!(bitmap == null /*&& mCurrentInternetConnection.equals("DISCONNECTED")*/)){
                /*curBook.setThumbnailBitmap(bitmap);
                mBooksAdapter.notifyItemChanged(curPos);*/
                result.add(bitmap);
                mUrls.remove(0);
            }
            else{
                break;
            }
        }

        return result;
    }

}
