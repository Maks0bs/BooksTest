package com.example.bookstest2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.R;//may not have to import

import java.util.ArrayList;



public class BooksAdapterRecycler extends RecyclerView.Adapter<BooksAdapterRecycler.ViewHolder> {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.book_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        TextView curTextViewBookTitle = viewHolder.getTextViewBookTitle();
        ImageView curImageViewThumbnail = viewHolder.getImageViewThumbnail();
        TextView curTextViewBookAuthors = viewHolder.getTextViewBookAuthors();
        TextView curTextViewEBookInfo = viewHolder.getTextViewEBookInfo();
        ImageView curImageViewRatingStar = viewHolder.getImageViewRatingStar();
        TextView curTextViewRatingNumber = viewHolder.getTextViewRatingNumber();
        TextView curTextViewBookPrice = viewHolder.getTextViewBookPrice();
        TextView mTextViewEmpty = viewHolder.getTextViewEmpty();
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

    @Override
    public int getItemCount(){
        return mDataSet.size();
    }

}
