package com.example.bookstest2.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bookstest2.R;
import com.example.bookstest2.fragments.BooksFragmentRecycler;
import com.example.bookstest2.fragments.GoogleplayFragment;
import com.example.bookstest2.fragments.HomeFragment;
import com.example.bookstest2.fragments.YoutubeFragment;
import com.example.bookstest2.utils.HTTPQueryUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Fragment fragmentHome = new HomeFragment();
    Fragment fragmentBooks = null;
    Fragment fragmentYoutube = null;
    Fragment fragmentGoogleplay = null;
    Fragment fragmentActive = fragmentHome;

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {


                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragmentToSwap = null;

                    switch(menuItem.getItemId()){
                        case R.id.navigation_home:
                            if (fragmentHome == null){
                                fragmentHome = new HomeFragment();
                                getSupportFragmentManager().beginTransaction().
                                        add(R.id.fragment_container, fragmentHome).commit();
                            }
                            fragmentToSwap = fragmentHome;
                            break;
                        case R.id.navigation_books:
                            if (fragmentBooks == null){
                                fragmentBooks = new BooksFragmentRecycler(
                                        getResources().getString(R.string.navigation_books_str),
                                        getResources().getString(R.string.books_search_hint)
                                );
                                getSupportFragmentManager().beginTransaction().
                                        add(R.id.fragment_container, fragmentBooks).commit();
                            }
                            fragmentToSwap = fragmentBooks;
                            break;
                        case R.id.navigation_youtube:
                            if (fragmentYoutube == null){
                                fragmentYoutube = new YoutubeFragment(
                                        getResources().getString(R.string.navigation_youtube_str),
                                        getResources().getString(R.string.youtube_search_hint)

                                );
                                getSupportFragmentManager().beginTransaction().
                                        add(R.id.fragment_container, fragmentYoutube).commit();
                            }
                            fragmentToSwap = fragmentYoutube;
                            break;
                        case R.id.navigation_googleplay:
                            if (fragmentGoogleplay == null){
                                fragmentGoogleplay = new GoogleplayFragment(
                                        getResources().getString(R.string.navigation_googleplay_str),
                                        getResources().getString(R.string.googleplay_search_hint)
                                );
                                getSupportFragmentManager().beginTransaction().
                                        add(R.id.fragment_container, fragmentGoogleplay).commit();
                            }
                            fragmentToSwap = fragmentGoogleplay;
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().
                            hide(fragmentActive).
                            show(fragmentToSwap).commit();
                    fragmentActive = fragmentToSwap;

                    return true;
                }

            };





    /*TODO
    *   !!!IMPORTANT: it may not be possible to create loaders with same IDs in each fragment
    *   *
    *   *
    *   Urgent: fix problem with visibility of RecycleView at first (due to RefreshLayout)
    *   *
    *   *** Block of most likely fixed bugs, but they don't let me sleep at night ***
    *       * when you leave application (press) home and turn off internet and then go to app again - shit happens
    *           this has been fixed in the onLoadFInished by removing all loading_footers - but i don't like this solution - there has to be another one
    *       * when moving to the list of opened apps, while loading the request, some shit happens - more items get counted as added to array, but are not added, so the next response has a shift in regard to the books id
    *       * handle showing no internet sign when internet is turned off while loading (when bitmaps don't have time to get downloaded)
    *       * when going to main screen, while loading, loader gets called 2 times, but onLoadMore only one time (wtf), one element in ArrayList disappears
    *       * there may be some other bugs that I'm not currently aware of and it sucks
    *   ******
    *   *
    *   Urgent: fix Bitmap loading in the case, described above
    *   2) Look to do in HTTPQUERYUTILS
    *   3) Implement api logic!!!
    *   4) Create separate QueryUtils subclasses for each query
    *   5) Add swipe up to refresh
    *   6) Visual polish
    *   7) Code polish!!!
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().
                add(R.id.fragment_container, fragmentHome).
                show(fragmentHome).commit();

        HTTPQueryUtils.BooksQueryManager query = new HTTPQueryUtils.BooksQueryManager("TEST");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemListener);
    }


}
