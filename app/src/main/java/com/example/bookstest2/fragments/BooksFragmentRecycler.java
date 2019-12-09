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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.espresso.action.Swipe;

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
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
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
    private String mCurrentInternetConnection = null;
    private static int mLoadingPosition = 0;

    public static void updateLoadingPosition(int value){
        mLoadingPosition += value;
    }

    //private int mCurrent

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
                if (mLoaderManager.hasRunningLoaders() ||
                        mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(BooksVolume.LOADING_FOOTER)){//this check has been added recently and may fix the problem, but i don't like this solution
                    return;
                }
                Log.e("LOADMORE", page + " " + mLoadingPosition);
                Bundle bundle = new Bundle();
                bundle.putString("query", mCurrentQuery);
                bundle.putInt("startIndex", mLoadingPosition);//!!!
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

                    if(networkInfo != null &&
                            (networkInfo.getState() == NetworkInfo.State.CONNECTED ||
                             networkInfo.getState() == NetworkInfo.State.CONNECTING)) {
                        mCurrentInternetConnection = "CONNECTED";
                        Log.e("app", "Network " + networkInfo.getTypeName()+" connected");
                        mEmptyTextViewStart.setVisibility(View.GONE);
                        if (mLoaderManager.getLoader(0) == null/*!mLoaderCreated*/){
                            initQueryListeners();
                        }


                        if (mArrayListBooks.size() > 0 &&
                                mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(
                                BooksVolume.NO_INTERNET_AVAILABLE)){
                            mArrayListBooks.remove(mArrayListBooks.size() - 1);
                            mBooksAdapter.notifyItemRemoved(mArrayListBooks.size());

                        }
                        else if (mArrayListBooks.size() > 0 &&
                                mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(
                                BooksVolume.LOADING_FOOTER)){
                            //mLoaderManager.destroyLoader();
                            Log.e("initQueryReceivers", "last element in array is LOADING_FOOTER");
                        }

                        mRecyclerViewSearchResults.addOnScrollListener(mRecyclerScrollListener);
                        mToolbarSearchView.setOnQueryTextListener(mSearchViewTextListener);
                        mEmptyTextViewStart.setVisibility(View.GONE);
                    }
                    else if (networkInfo != null &&
                            (networkInfo.getState() == NetworkInfo.State.DISCONNECTED ||
                             networkInfo.getState() == NetworkInfo.State.DISCONNECTING)) {//TODO may need to hande DISCONNECTING seperately
                        mCurrentInternetConnection = "DISCONNECTED";
                        mToolbarSearchView.setOnQueryTextListener(null);
                        mRecyclerViewSearchResults.removeOnScrollListener(mRecyclerScrollListener);
                        if (mArrayListBooks.size() == 0 && !mLoaderManager.hasRunningLoaders()/*mLoaderManager.getLoader(0) == null*/){
                            mEmptyTextViewStart.setVisibility(View.VISIBLE);
                            mEmptyTextViewStart.setText("NO INTERNET");
                        }
                        else if (mArrayListBooks.size() == 0){
                            //DO NOTHING
                        }
                        else{
                            if (mLoaderManager.hasRunningLoaders() &&
                                mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(
                                BooksVolume.LOADING_FOOTER)){

                                //TODO THIS MAY NOT BE A GOOD SOLUTION FOR LOADING PROBLEM
                                /*int curPos = 0;
                                while(curPos < mArrayListBooks.size() - 1){
                                    if (mArrayListBooks.get(curPos).getTitle().equals(
                                            BooksVolume.LOADING_FOOTER)){

                                        mArrayListBooks.remove(curPos);
                                        mBooksAdapter.notifyItemRemoved(curPos);
                                    }
                                    else{
                                        curPos++;
                                    }
                                }*/
                                mArrayListBooks.set(mArrayListBooks.size() - 1,
                                        new BooksVolume(BooksVolume.NO_INTERNET_AVAILABLE));
                                mBooksAdapter.notifyItemChanged(mArrayListBooks.size() - 1);
                            }
                            else{
                                mArrayListBooks.add(new BooksVolume(BooksVolume.NO_INTERNET_AVAILABLE));
                                mBooksAdapter.notifyItemInserted(mArrayListBooks.size());
                            }
                            //TODO see how to handle the change of loader:
                            //mLoaderManager.destroyLoader(0);
                        }

                    }
                    else if (networkInfo == null ||
                             networkInfo.getState() == NetworkInfo.State.UNKNOWN){
                        mCurrentInternetConnection = "UNKNOWN";
                        mEmptyTextViewStart.setVisibility(View.VISIBLE);
                        mEmptyTextViewStart.setText("NO INFO ABOUT INTERNET");
                    }
                }
                /*if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    Log.d("app","There's no network connectivity");
                }*/
                else{
                    //TODO understand why this case may be possible and why it will be activated
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.SwipeRefreshLayout_1);
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

        //mSwipeRefreshLayout.ref

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
        //mLoaderWorking = true;

        boolean newQuery = args.getBoolean("newQuery");
        if (newQuery){
            updateLoadingPosition(-mLoadingPosition);
            mArrayListBooks.clear();
            mLoadingIndicatorStart.setVisibility(View.VISIBLE);
        }
        else{
            /*mArrayListBooks.add(new BooksVolume(BooksVolume.LOADING_FOOTER));
            mBooksAdapter.notifyItemInserted(mArrayListBooks.size() - 1);*/
            //mBooksAdapter.createViewHolder(mRecyclerViewSearchResults, 1);
            mArrayListBooks.add(new BooksVolume(BooksVolume.LOADING_FOOTER));
            Log.e("LOADING FOOTER", "added to arrayList");
            mBooksAdapter.notifyItemInserted(mArrayListBooks.size());
            //handle viewType in oncreateView and ViewHolder constructor
        }

        String searchQueryStr = args.getString("query");
        /*String inputUrlStr = HTTPQueryUtils.BOOKS_API_START_STR;
        inputUrlStr = inputUrlStr + "v1/volumes?q=" + searchQueryStr +
                "&startIndex=" + String.valueOf(args.getInt("startIndex"));*/

        Loader<ArrayList<BooksVolume>> result = new
                BooksLoader(getActivity(), searchQueryStr, args.getInt("startIndex"));



        return result;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<BooksVolume>> loader, ArrayList<BooksVolume> data) {
        //clearAdapter
        //mArrayListBooks.clear();

        Log.e("onLoadFinished", "entered " + mNetworkChangeReceiver.getResultData());

        mLoadingIndicatorStart.setVisibility(View.GONE);

        if (mArrayListBooks.size() > 0 &&
                (mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(BooksVolume.LOADING_FOOTER) ||
                 mArrayListBooks.get(mArrayListBooks.size() - 1).getTitle().equals(BooksVolume.NO_INTERNET_AVAILABLE))){
            mArrayListBooks.remove(mArrayListBooks.size() - 1);
            mBooksAdapter.notifyItemRemoved(mArrayListBooks.size());
            /*mBooksAdapter.notifyItemChanged(mArrayListBooks.size() - 9,);*/
        }
        if (data != null){
            mArrayListBooks.addAll(data);
        }

        if (mCurrentInternetConnection.equals("DISCONNECTED")){
            int curPos = 0;
            while(curPos < mArrayListBooks.size()){
                if (mArrayListBooks.get(curPos).getTitle().equals(
                        BooksVolume.LOADING_FOOTER)){

                    mArrayListBooks.remove(curPos);
                    mBooksAdapter.notifyItemRemoved(curPos);
                }
                else{
                    curPos++;
                }
            }
            mArrayListBooks.add(new BooksVolume(BooksVolume.NO_INTERNET_AVAILABLE));
        }



        for (int i = 0; i < mArrayListBooks.size(); i++){
            Log.e("ARRAYLISTELEMENTS", i + " " + mArrayListBooks.get(i).getTitle() + " " + mArrayListBooks.get(i).getThumbnailBitmap());
        }
        Log.e("-----", "------------");

        mLoaderManager.destroyLoader(0); //TODO looks like this fixes the problem, but the solution seems to be memory-inefficient

        Log.e("LOADER", "finished");


        //mLoaderWorking = false;
    }



    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<BooksVolume>> loader) {
        Log.e("LOADER", "was reset");
        mArrayListBooks.clear();
        mRecyclerScrollListener.resetState();
        mLoadingIndicatorStart.setVisibility(View.VISIBLE);
    }
}
