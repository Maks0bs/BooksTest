package com.example.bookstest2.utils;

import android.graphics.Bitmap;

public class BooksVolume {
    private String mTitle = "";
    private String mAuthor = "";
    private boolean mIsEBook = false;
    private String mPrice = ""; //may need to change to smth else, there are different currencies that vary from location
    private String mThumbnailUrlStr = "";
    private double mRating;
    private Bitmap mThumbnailBitmap = null;
    public static final double NO_RATING_PROVIDED = -1.0;//change to xml string value
    public static final String IS_EBOOK = "E-book";
    public static final String IS_NOT_EBOOK = "-";
    public static final String NO_PRICE_PROVIDED = "Not for sale";

    public BooksVolume(String title, String author, boolean isEBook, String price, double rating){
        mTitle = title;
        mAuthor = author;
        mIsEBook = isEBook;
        mPrice = price;
        mRating = rating;
    }
    public BooksVolume(String title, String author, boolean isEBook, String price, double rating, Bitmap thumbnail){
        mTitle = title;
        mAuthor = author;
        mIsEBook = isEBook;
        mPrice = price;
        mRating = rating;
        mThumbnailBitmap = thumbnail;
    }

    public String getTitle(){
        return mTitle;
    }
    public String getAuthor(){
        return mAuthor;
    }
    public boolean getIsEBook(){
        return mIsEBook;
    }
    public String getPrice(){
        return mPrice;
    }
    public double getRating(){
        return mRating;
    }
    public Bitmap getThumbnailBitmap(){
        return mThumbnailBitmap;
    }
}
