package com.example.bookstest2.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.bookstest2.BooksVolume;
import com.example.bookstest2.HTTPQueryUtils;
import com.example.bookstest2.R;
import com.example.bookstest2.adapters.BooksAdapter;
import com.example.bookstest2.loaders.BooksLoader;

import java.util.ArrayList;

//testing this for commit

//testing for another commit

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BooksVolume>> {
    private View mRootView = null;
    private SearchView mToolbarSearchView = null;
    private RelativeLayout.LayoutParams mSearchViewLayoutParams = null;
    private View.OnClickListener mSearchViewClickListener = null;
    private SearchView.OnCloseListener mSearchViewCloseListener = null;
    private TextView mTextViewFragmentName = null;
    private ListView mListViewSearchResults = null;
    private ArrayList<BooksVolume> mArrayListBooks = null;
    private Context mContext = null; //may need to pass context to constructor!!!
    private BooksAdapter mBooksAdapter = null;
    private String mToolbarTitle = null;
    private String mSearchHint = null;//changed all types to static

    @Override
    public Loader<ArrayList<BooksVolume>> onCreateLoader(int id, Bundle args){
        //TODO temporary query, has to be put in through searchView on ToolBar
        String searchQueryStr = "check";
        String inputUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
        inputUrlStr = inputUrlStr + "v1/volumes?q=" + searchQueryStr;
        return new BooksLoader(getActivity(),inputUrlStr);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<BooksVolume>> loader, ArrayList<BooksVolume> data) {
        mBooksAdapter.clear();
        mBooksAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<BooksVolume>> loader) {
        mBooksAdapter.clear();
    }

    public SearchFragment(String toolbarTitle, String searchHint){
        mToolbarTitle = toolbarTitle;
        mSearchHint = searchHint;
    }

    private void initListeners(){
        mSearchViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TEST", "searched");
                mTextViewFragmentName.setVisibility(View.GONE);
                mSearchViewLayoutParams.addRule(RelativeLayout.RIGHT_OF,
                        (mRootView.findViewById(R.id.imageView_random)).getId());
                mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                if (Build.VERSION.SDK_INT >= 17){
                    mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                    mSearchViewLayoutParams.addRule(RelativeLayout.END_OF,
                            (mRootView.findViewById(R.id.imageView_random)).getId());
                }

                mSearchViewLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            }
        };

        mSearchViewCloseListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("TEST", "closed");
                mTextViewFragmentName.setVisibility(View.VISIBLE);
                mSearchViewLayoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                mSearchViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
                mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);

                if (Build.VERSION.SDK_INT >= 17){
                    mSearchViewLayoutParams.addRule(RelativeLayout.END_OF, 0);
                    mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
                }

                return false;
            }
        };


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(mToolbarTitle, "Created");
        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        mContext = container.getContext();

        mTextViewFragmentName = (TextView) mRootView.findViewById(R.id.textView_fragment_name);
        mTextViewFragmentName.setText(mToolbarTitle);
        mTextViewFragmentName.setVisibility(View.GONE);

        mToolbarSearchView = (SearchView) mRootView.findViewById(R.id.searchView_toolbar_search);
        mToolbarSearchView.setQueryHint(mSearchHint);
        mToolbarSearchView.setIconified(false);
        mSearchViewLayoutParams = (RelativeLayout.LayoutParams)mToolbarSearchView.getLayoutParams();
        mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

        if (Build.VERSION.SDK_INT >= 17){
            mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            mSearchViewLayoutParams.addRule(RelativeLayout.END_OF,
                    (mRootView.findViewById(R.id.imageView_random)).getId());
        }
        mSearchViewLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;

        initListeners();
        mToolbarSearchView.setOnSearchClickListener(mSearchViewClickListener);
        mToolbarSearchView.setOnCloseListener(mSearchViewCloseListener);

        /*mToolbarSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TEST", "searched");
                mTextViewFragmentName.setVisibility(View.GONE);
                mSearchViewLayoutParams.addRule(RelativeLayout.RIGHT_OF,
                        (rootView.findViewById(R.id.imageView_random)).getId());
                mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                if (Build.VERSION.SDK_INT >= 17){
                    mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                    mSearchViewLayoutParams.addRule(RelativeLayout.END_OF,
                            (rootView.findViewById(R.id.imageView_random)).getId());
                }

                mSearchViewLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            }
        });


        mToolbarSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("TEST", "closed");
                mTextViewFragmentName.setVisibility(View.VISIBLE);
                mSearchViewLayoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                mSearchViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
                mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);

                if (Build.VERSION.SDK_INT >= 17){
                    mSearchViewLayoutParams.addRule(RelativeLayout.END_OF, 0);
                    mSearchViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
                }

                return false;
            }
        });*/

        mListViewSearchResults = (ListView) mRootView.findViewById(R.id.listView_search_results);

        mArrayListBooks = new ArrayList<BooksVolume>();
        /*TODO
        *   retrieve info from api here
        *   set up "swipe to update" and loading 10 at a time to work here
        */

        mBooksAdapter = new BooksAdapter(container.getContext(), mArrayListBooks);
        mListViewSearchResults.setAdapter(mBooksAdapter);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, SearchFragment.this/*mCOntext...*/);



        return mRootView;
    }
}
