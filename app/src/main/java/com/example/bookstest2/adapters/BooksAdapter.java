package com.example.bookstest2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookstest2.BooksVolume;
import com.example.bookstest2.R;//may not have to import

import java.util.ArrayList;

public class BooksAdapter extends ArrayAdapter<BooksVolume> {
    public BooksAdapter(Context context, ArrayList<BooksVolume> array){
        super(context, 0, array);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if(listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item, parent, false);
        }

        String curBookVolume = getItem(position).getTitle();
        TextView textViewBookTitle = listView.findViewById(R.id.TextView_book_title);
        textViewBookTitle.setText(curBookVolume);

        return listView;
    }
}
