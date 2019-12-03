package com.example.bookstest2.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;
import com.example.bookstest2.R;
import com.example.bookstest2.adapters.BooksAdapter;
import com.example.bookstest2.loaders.BooksLoader;
import com.example.bookstest2.utils.NetworkStateReceiver;
import com.example.bookstest2.utils.QueryTextUtils;

import java.util.ArrayList;

public class BooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BooksVolume>> {
    private View mRootView = null;
    private LoaderManager mLoaderManager= null;
    private SearchView mToolbarSearchView = null;
    private RelativeLayout.LayoutParams mSearchViewLayoutParams = null;
    private View.OnClickListener mSearchViewClickListener = null;
    private SearchView.OnCloseListener mSearchViewCloseListener = null;
    private SearchView.OnQueryTextListener mSearchViewTextListener = null;
    private TextView mTextViewFragmentName = null;
    private TextView mEmptyTextViewStart = null;
    private ListView mListViewSearchResults = null;
    private ProgressBar mLoadingIndicatorStart = null;
    private ArrayList<BooksVolume> mArrayListBooks = null;
    private Context mContext = null; //may need to pass context to constructor!!!
    private BooksAdapter mBooksAdapter = null;
    private BroadcastReceiver mNetworkChangeReceiver = null;
    private String mToolbarTitle = null;
    private String mSearchHint = null;//changed all types to static
    private boolean mLoaderCreated = false;
    private boolean mInternetAvailable = true;

    public BooksFragment(String toolbarTitle, String searchHint){
        mToolbarTitle = toolbarTitle;
        mSearchHint = searchHint;
    }

    private void initQueryListeners(){
        mSearchViewTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query", QueryTextUtils.prepareQueryForSubmission(query));
                if (!mLoaderCreated){
                    mLoaderManager.initLoader(0, bundle, BooksFragment.this);
                    mLoaderCreated = true;
                }
                else{
                    mLoaderManager.restartLoader(0, bundle, BooksFragment.this);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    private void initQueryReceivers(){
        mNetworkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("app","Network connectivity change");
                if (intent.getExtras() != null) {
                    NetworkInfo networkInfo = (NetworkInfo)
                            intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);

                    if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        Log.e("app", "Network " + networkInfo.getTypeName()+" connected");
                        mEmptyTextViewStart.setVisibility(View.GONE);
                        if (!mLoaderCreated){
                            initQueryListeners();
                            mToolbarSearchView.setOnQueryTextListener(mSearchViewTextListener);
                        }
                        mEmptyTextViewStart.setVisibility(View.GONE);
                    }
                    else
                    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                        mToolbarSearchView.setOnQueryTextListener(null);
                        mEmptyTextViewStart.setVisibility(View.VISIBLE);
                        mEmptyTextViewStart.setText("NO INTERNET");
                    }
                }
                /*if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    Log.d("app","There's no network connectivity");
                }*/
                else{
                    /*TODO show "no internet" error message if on start (ListView is empty)
                     *    in the other case show this error at the on of the listView (new items to be loaded)
                     */
                }
            }
        };
    }

    private void initLayoutListeners(){
        mSearchViewClickListener = new View.OnClickListener(){
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

        mToolbarSearchView.setOnSearchClickListener(mSearchViewClickListener);
        mToolbarSearchView.setOnCloseListener(mSearchViewCloseListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(mToolbarTitle, "Created");
        //Retrieve all xmls stuff from root and inflate
        mRootView = inflater.inflate(R.layout.fragment_search, container, false);
        mToolbarSearchView = (SearchView) mRootView.findViewById(R.id.searchView_toolbar_search);
        mTextViewFragmentName = (TextView) mRootView.findViewById(R.id.textView_fragment_name);
        mListViewSearchResults = (ListView) mRootView.findViewById(R.id.listView_search_results);
        mEmptyTextViewStart = (TextView) mRootView.findViewById(R.id.TextView_empty);
        mLoadingIndicatorStart = (ProgressBar) mRootView.findViewById(R.id.ProgressBar_loading_start);
        mContext = container.getContext();

        //Initialize List and Adapters
        mArrayListBooks = new ArrayList<BooksVolume>();
        mBooksAdapter = new BooksAdapter(mContext, mArrayListBooks);
        mListViewSearchResults.setAdapter(mBooksAdapter);
        /*TODO
         *   retrieve info from api here
         *   set up "swipe to update" and loading 10 at a time to work here
         */

        mLoaderManager = getLoaderManager();

        //Initial state of the fragments name's textView
        mTextViewFragmentName.setText(mToolbarTitle);
        mTextViewFragmentName.setVisibility(View.GONE);

        mLoadingIndicatorStart.setVisibility(View.GONE);

        //Initial state of the SearchView
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

        //Check if internet is available

        //NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();

        initLayoutListeners();
        initQueryReceivers();
        IntentFilter connectivityChangeIntentFilter = new
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mNetworkChangeReceiver, connectivityChangeIntentFilter);

        return mRootView;
    }

    @Override
    public Loader<ArrayList<BooksVolume>> onCreateLoader(int id, Bundle args){
        mBooksAdapter.clear();
        String searchQueryStr = args.getString("query");
        String inputUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
        inputUrlStr = inputUrlStr + "v1/volumes?q=" + searchQueryStr;
        Log.e("LOADER", "loader created");

        return new BooksLoader(getActivity(),inputUrlStr);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<BooksVolume>> loader, ArrayList<BooksVolume> data) {
        mBooksAdapter.clear();
        mBooksAdapter.addAll(data);
        mLoadingIndicatorStart.setVisibility(View.GONE);

        Log.e("LOADER", "finished");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<BooksVolume>> loader) {
        mBooksAdapter.clear();
        Log.e("LOADER", "was reset");
    }
}
