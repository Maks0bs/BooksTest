package com.example.bookstest2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookstest2.R;

public class HomeFragment extends Fragment {
    private final String DEFAULT_TITLE = "Home";

    private TextView mTextViewFragmentName = null;
    private String mToolbarTitle = null;

    // no loader needed in this fragment!!!

    public HomeFragment(String toolbarTitle){
        mToolbarTitle = toolbarTitle;
    }
    public HomeFragment(){
        mToolbarTitle = DEFAULT_TITLE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Home", "Created");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mTextViewFragmentName = (TextView) rootView.findViewById(R.id.textView_fragment_name);//may be problemtic because id names are the same as in BooksFragment
        mTextViewFragmentName.setText(mToolbarTitle);

        return rootView;
    }
}
