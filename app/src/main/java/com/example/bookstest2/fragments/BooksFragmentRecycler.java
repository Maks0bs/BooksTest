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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstest2.adapters.BooksAdapterRecycler;
import com.example.bookstest2.listeners.EndlessRecyclerViewScrollListener;
import com.example.bookstest2.utils.BooksVolume;
import com.example.bookstest2.utils.HTTPQueryUtils;
import com.example.bookstest2.R;
import com.example.bookstest2.adapters.BooksAdapter;
import com.example.bookstest2.loaders.BooksLoader;
import com.example.bookstest2.utils.NetworkStateReceiver;
import com.example.bookstest2.utils.QueryTextUtils;

import java.util.ArrayList;

public class BooksFragmentRecycler extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<BooksVolume>> {
    private View mRootView = null;
    private LoaderManager mLoaderManager= null;
    private SearchView mToolbarSearchView = null;
    private RelativeLayout.LayoutParams mSearchViewLayoutParams = null;
    private View.OnClickListener mSearchViewClickListener = null;
    private SearchView.OnCloseListener mSearchViewCloseListener = null;
    private SearchView.OnQueryTextListener mSearchViewTextListener = null;
    private TextView mTextViewFragmentName = null;
    private TextView mEmptyTextViewStart = null;
    //private ListView mListViewSearchResults = null;
    private RecyclerView mRecyclerViewSearchResults = null;
    private EndlessRecyclerViewScrollListener mRecyclerScrollListener = null;
    private ProgressBar mLoadingIndicatorStart = null;
    private ArrayList<BooksVolume> mArrayListBooks = null;
    private Context mContext = null; //may need to pass context to constructor!!!
    private BooksAdapterRecycler mBooksAdapter = null;
    private RecyclerView.LayoutManager mLayoutManager = null;
    private BroadcastReceiver mNetworkChangeReceiver = null;
    private String mToolbarTitle = null;
    private String mCurrentQuery = null;
    private String mSearchHint = null;//changed all types to static
    private boolean mLoaderWorking = false; //TODO may need to change to smth else to work propelrly!!!

    public boolean getLoaderWorking(){
        return mLoaderWorking;
    }

    public BooksFragmentRecycler(String toolbarTitle, String searchHint){
        mToolbarTitle = toolbarTitle;
        mSearchHint = searchHint;
    }

    //TODO put all initListener and initReceivers in a public class with public methods in utils package

    private void initQueryListeners(){
        mSearchViewTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query", QueryTextUtils.prepareQueryForSubmission(query));
                bundle.putBoolean("newQuery", true);
                bundle.putInt("startIndex", 0);//!!!
                mCurrentQuery = query;
                if (mLoaderManager.getLoader(0) == null){
                    mLoaderManager.initLoader(0, bundle, BooksFragmentRecycler.this);
                    //mLoaderCreated = true;
                    Log.e("initQuery", "initialized");
                }
                else{
                    //mLoaderManager.destroyLoader(0);
                    mLoaderManager.restartLoader(0, bundle, BooksFragmentRecycler.this);
                    Log.e("initQuery", "restarted");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        mRecyclerScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //loadNextDataFromApi(page);
                if (mLoaderWorking){
                    return;
                }
                Log.e("LOADMORE", page + " " + totalItemsCount);
                Bundle bundle = new Bundle();
                bundle.putString("query", mCurrentQuery);
                bundle.putInt("startIndex", totalItemsCount);//!!!
                bundle.putBoolean("newQuery", false);
                if (mLoaderManager.getLoader(0) == null){
                    mLoaderManager.initLoader(0, bundle, BooksFragmentRecycler.this);
                }
                else{
                    mLoaderManager.restartLoader(0, bundle, BooksFragmentRecycler.this);
                }

                //TODO implement loading next data, maybe with the same method as original loading
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
                        if (mLoaderManager.getLoader(0) == null/*!mLoaderCreated*/){
                            initQueryListeners();
                            mToolbarSearchView.setOnQueryTextListener(mSearchViewTextListener);
                            mRecyclerViewSearchResults.addOnScrollListener(mRecyclerScrollListener);
                        }
                        else{
                            if (mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(
                                    BooksVolume.NO_INTERNET_AVAILABLE)){
                                mArrayListBooks.remove(mArrayListBooks.size() - 1);
                                mBooksAdapter.notifyItemRemoved(mArrayListBooks.size());
                                mRecyclerScrollListener.onLoadMore(
                                        0,
                                        mArrayListBooks.size() + 1,
                                        mRecyclerViewSearchResults
                                );
                            }
                            mToolbarSearchView.setOnQueryTextListener(mSearchViewTextListener);
                        }
                        mEmptyTextViewStart.setVisibility(View.GONE);
                    }
                    else
                    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                        mToolbarSearchView.setOnQueryTextListener(null);
                        if (mLoaderManager.getLoader(0) == null){
                            mEmptyTextViewStart.setVisibility(View.VISIBLE);
                            mEmptyTextViewStart.setText("NO INTERNET");
                        }
                        else{
                            if (mLoaderWorking){
                                mArrayListBooks.set(mArrayListBooks.size() - 1,
                                        new BooksVolume(BooksVolume.NO_INTERNET_AVAILABLE));
                                mBooksAdapter.notifyItemChanged(mArrayListBooks.size() - 1);
                            }
                            else{
                                mArrayListBooks.add(new BooksVolume(BooksVolume.NO_INTERNET_AVAILABLE));
                                mBooksAdapter.notifyItemInserted(mArrayListBooks.size());
                            }
                        }

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(mToolbarTitle, "Created");
        //Retrieve all xmls stuff from root and inflate
        mRootView = inflater.inflate(R.layout.fragment_search_recycler, container, false);
        mToolbarSearchView = (SearchView) mRootView.findViewById(R.id.searchView_toolbar_search);
        mTextViewFragmentName = (TextView) mRootView.findViewById(R.id.textView_fragment_name);
        //mListViewSearchResults = (ListView) mRootView.findViewById(R.id.listView_search_results);
        mRecyclerViewSearchResults = (RecyclerView) mRootView.findViewById(R.id.RecyclerView_search_results);
        mEmptyTextViewStart = (TextView) mRootView.findViewById(R.id.TextView_empty);
        mLoadingIndicatorStart = (ProgressBar) mRootView.findViewById(R.id.ProgressBar_loading_start);
        mLoaderManager = getActivity().getSupportLoaderManager();
        mLayoutManager = new LinearLayoutManager(getActivity());//doesnt have to linear
        mRecyclerViewSearchResults.setLayoutManager(mLayoutManager);

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

        //Initialize List and Adapters
        mArrayListBooks = new ArrayList<BooksVolume>();
        mBooksAdapter = new BooksAdapterRecycler(mArrayListBooks);
        mRecyclerViewSearchResults.setAdapter(mBooksAdapter);//change adapter
        /*TODO
         *   retrieve info from api here
         *   set up "swipe to update" and loading 10 at a time to work here
         */

        //Check if internet is available
        //NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();

        initLayoutListeners();
        mToolbarSearchView.setOnSearchClickListener(mSearchViewClickListener);
        mToolbarSearchView.setOnCloseListener(mSearchViewCloseListener);

        initQueryReceivers();
        IntentFilter connectivityChangeIntentFilter = new
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mNetworkChangeReceiver, connectivityChangeIntentFilter);

        return mRootView;
    }

    /*TODO loader should only work with 1 loader with id = 0, so that the same process loads initial
     * values and further loading

     */

    @Override
    public Loader<ArrayList<BooksVolume>> onCreateLoader(int id, Bundle args){
        Log.e("LOADER", "loader created");
        mLoaderWorking = true;

        boolean newQuery = args.getBoolean("newQuery");
        if (newQuery){
            mArrayListBooks.clear();
            mLoadingIndicatorStart.setVisibility(View.VISIBLE);
        }
        else{
            /*mArrayListBooks.add(new BooksVolume(BooksVolume.LOADING_FOOTER));
            mBooksAdapter.notifyItemInserted(mArrayListBooks.size() - 1);*/
            //mBooksAdapter.createViewHolder(mRecyclerViewSearchResults, 1);
            mArrayListBooks.add(new BooksVolume(BooksVolume.LOADING_FOOTER));
            mBooksAdapter.notifyItemInserted(mArrayListBooks.size());
            //handle viewType in oncreateView and ViewHolder constructor
        }

        String searchQueryStr = args.getString("query");
        String inputUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
        inputUrlStr = inputUrlStr + "v1/volumes?q=" + searchQueryStr +
                "&startIndex=" + String.valueOf(args.getInt("startIndex"));

        return new BooksLoader(getActivity(), inputUrlStr);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<BooksVolume>> loader, ArrayList<BooksVolume> data) {
        //clearAdapter
        //mArrayListBooks.clear();

        mLoadingIndicatorStart.setVisibility(View.GONE);

        if (mArrayListBooks.size() > 0){
            mArrayListBooks.remove(mArrayListBooks.size() - 1);
            mBooksAdapter.notifyItemRemoved(mArrayListBooks.size());
            /*mBooksAdapter.notifyItemChanged(mArrayListBooks.size() - 9,);*/
        }
        if (data != null){
            mArrayListBooks.addAll(data);
        }

        for (int i = 0; i < mArrayListBooks.size(); i++){
            Log.e("ARRAYLISTELEMENTS", i + " " + mArrayListBooks.get(i).getTitle());
        }
        Log.e("-----", "------------");
        Log.e("LOADER", "finished");

        mLoaderWorking = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<BooksVolume>> loader) {
        Log.e("LOADER", "was reset");
        mArrayListBooks.clear();
        mRecyclerScrollListener.resetState();
        mLoadingIndicatorStart.setVisibility(View.VISIBLE);
    }
}
