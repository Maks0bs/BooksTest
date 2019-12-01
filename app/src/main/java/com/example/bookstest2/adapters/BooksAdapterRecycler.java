package com.example.bookstest2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.R;//may not have to import

import java.util.ArrayList;



public class BooksAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<BooksVolume> mDataSet = null;

    public BooksAdapterRecycler(ArrayList<BooksVolume> data){
        mDataSet = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextViewBookTitle = null;
        private ImageView mImageViewThumbnail = null;
        private TextView mTextViewBookAuthors = null;
        private TextView mTextViewEBookInfo = null;
        private ImageView mImageViewRatingStar = null;
        private TextView mTextViewRatingNumber = null;
        private TextView mTextViewBookPrice = null;
        private TextView mTextViewEmpty = null;


        public ViewHolder(View v){
            super(v);
            /*if (v.findViewById(R.id.ImageView_book_thumbnail) == null){//TODO may need to change this to smth normal
                mProgressBarEmpty = v.findViewById(R.id.ProgressBar_empty);
                return;
            }*/

            mImageViewThumbnail = v.findViewById(R.id.ImageView_book_thumbnail);
            mTextViewBookTitle = v.findViewById(R.id.TextView_book_title);
            mTextViewBookAuthors = v.findViewById(R.id.TextView_book_authors);
            mImageViewRatingStar = v.findViewById(R.id.ImageView_rating_star);
            mTextViewEBookInfo = v.findViewById(R.id.TextView_ebook_info);
            mTextViewRatingNumber = v.findViewById(R.id.TextView_rating_number);
            mTextViewBookPrice = v.findViewById(R.id.TextView_book_price);
            mTextViewEmpty = v.findViewById(R.id.TextView_list_empty);

        }

        public TextView getTextViewBookTitle(){
            return mTextViewBookTitle;
        }
        public ImageView getImageViewThumbnail(){
            return mImageViewThumbnail;
        }
        public TextView getTextViewBookAuthors(){
            return mTextViewBookAuthors;
        }
        public TextView getTextViewEBookInfo(){
            return mTextViewEBookInfo;
        }
        public ImageView getImageViewRatingStar(){
            return mImageViewRatingStar;
        }
        public TextView getTextViewRatingNumber(){
            return mTextViewRatingNumber;
        }
        public TextView getTextViewBookPrice(){
            return mTextViewBookPrice;
        }
        public TextView getTextViewEmpty(){
            return mTextViewEmpty;
        }
    }



    public static class ViewFooter extends RecyclerView.ViewHolder{
        private ProgressBar mProgressBarEmpty = null;


        public ViewFooter(View v){
            super(v);
            if (v.findViewById(R.id.ImageView_book_thumbnail) == null){//TODO may need to change this to smth normal
                mProgressBarEmpty = v.findViewById(R.id.ProgressBar_empty);
                return;
            }
            mProgressBarEmpty = v.findViewById(R.id.ProgressBar_empty);
        }

        public ProgressBar getProgressBarEmpty() {
            return mProgressBarEmpty;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getTitle().equals(BooksVolume.LOADING_FOOTER)){
            return 1;//TODO should change to static constants
        }
        else{
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){//TODO make footer work with determining viewtype and acting accordingly
        View v;
        switch (viewType){
            //TODO change to static constants
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.book_item, parent, false);
                return new ViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recycleview_footer, parent, false);
                return new ViewFooter(v);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        if (getItemViewType(position) == 0){
            ViewHolder vh = (ViewHolder) viewHolder;

            TextView curTextViewBookTitle = vh.getTextViewBookTitle();
            ImageView curImageViewThumbnail = vh.getImageViewThumbnail();
            TextView curTextViewBookAuthors = vh.getTextViewBookAuthors();
            TextView curTextViewEBookInfo = vh.getTextViewEBookInfo();
            ImageView curImageViewRatingStar = vh.getImageViewRatingStar();
            TextView curTextViewRatingNumber = vh.getTextViewRatingNumber();
            TextView curTextViewBookPrice = vh.getTextViewBookPrice();
            TextView curTextViewEmpty = vh.getTextViewEmpty();

            BooksVolume curBookVolume = mDataSet.get(position);

            curImageViewThumbnail.setImageBitmap(curBookVolume.getThumbnailBitmap());

            curTextViewBookTitle.setText(curBookVolume.getTitle());
            curTextViewBookAuthors.setText(curBookVolume.getAuthor());
            if (curBookVolume.getIsEBook()){
                curTextViewEBookInfo.setText("E-Book");//TODO change hardcoded str
            }
            else{
                curTextViewEBookInfo.setVisibility(View.GONE);
            }

            if (curBookVolume.getRating() == BooksVolume.NO_RATING_PROVIDED){
                curTextViewRatingNumber.setText("Not rated");//TODO change hardcoded str
                curImageViewRatingStar.setVisibility(View.GONE);
            }
            else{
                curTextViewRatingNumber.setText(String.valueOf(curBookVolume.getRating()));
            }

            if (curBookVolume.getPrice().equals(BooksVolume.NO_PRICE_PROVIDED)){
                curTextViewBookPrice.setText("Not for sale");//TODO change hardcoded str
            }
            else{
                curTextViewBookPrice.setText(curBookVolume.getPrice());
            }
        }
        else{
            /*ViewFooter vh = (ViewFooter) viewHolder;
            ProgressBar curProgressBarEmpty = vh.getProgressBarEmpty();*/
        }



        //Log.e("BOOK TITLE LOADED ADAPT", curBookVolume.getTitle());


    }

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }

}
