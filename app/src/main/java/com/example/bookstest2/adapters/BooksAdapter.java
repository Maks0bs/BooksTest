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

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.R;//may not have to import

import java.util.ArrayList;


public class BooksAdapter extends ArrayAdapter<BooksVolume> {
    public BooksAdapter(Context context, ArrayList<BooksVolume> array){
        super(context, 0, array);
    }
    private TextView mTextViewBookTitle = null;
    private ImageView mImageViewThumbnail = null;
    private TextView mTextViewBookAuthors = null;
    private TextView mTextViewEBookInfo = null;
    private ImageView mImageViewRatingStar = null;
    private TextView mTextViewRatingNumber = null;
    private TextView mTextViewBookPrice = null;
    private TextView mTextViewEmpty = null;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item, parent, false);
        }
        BooksVolume curBookVolume = getItem(position);

        mImageViewThumbnail = listView.findViewById(R.id.ImageView_book_thumbnail);
        mTextViewBookTitle = listView.findViewById(R.id.TextView_book_title);
        mTextViewBookAuthors = listView.findViewById(R.id.TextView_book_authors);
        mImageViewRatingStar = listView.findViewById(R.id.ImageView_rating_star);
        mTextViewEBookInfo = listView.findViewById(R.id.TextView_ebook_info);
        mTextViewRatingNumber = listView.findViewById(R.id.TextView_rating_number);
        mTextViewBookPrice = listView.findViewById(R.id.TextView_book_price);
        mTextViewEmpty = listView.findViewById(R.id.TextView_list_empty);

        mImageViewThumbnail.setImageBitmap(curBookVolume.getThumbnailBitmap());

        mTextViewBookTitle.setText(curBookVolume.getTitle());
        mTextViewBookAuthors.setText(curBookVolume.getAuthor());
        if (curBookVolume.getIsEBook()){
            mTextViewEBookInfo.setText("E-Book");//TODO change hardcoded str
        }
        else{
            mTextViewEBookInfo.setVisibility(View.GONE);
        }

        if (curBookVolume.getRating() == BooksVolume.NO_RATING_PROVIDED){
            mTextViewRatingNumber.setText("Not rated");//TODO change hardcoded str
            mImageViewRatingStar.setVisibility(View.GONE);
        }
        else{
            mTextViewRatingNumber.setText(String.valueOf(curBookVolume.getRating()));
        }

        if (curBookVolume.getPrice().equals(BooksVolume.NO_PRICE_PROVIDED)){
            mTextViewBookPrice.setText("Not for sale");//TODO change hardcoded str
        }
        else{
            mTextViewBookPrice.setText(curBookVolume.getPrice());
        }

        return listView;
    }
}
