package com.example.android.news;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.content.Context;

import android.content.Intent;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saksham on 22-01-2018.
 */

public class SportsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<word>> {
    private wordadapter adapter = null;
    private TextView emptytext;
    private Button retry;
    public static int index = -1;
    public static int top = -1;
    ListView list;
    private View loadingIndicator;
    public static final String LOG_TAG = utils.class.getSimpleName();

    private static final String USGS_REQUEST_URL = "https://newsapi.org/v2/top-headlines?sources=bbc-sport&apiKey=b5bfe33e18c943d7bc7a757b1760160c";


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_news_,container,false);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);

        list = (ListView) rootView.findViewById(R.id.list);
        if(savedInstanceState!=null) {
            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
        }
        emptytext = (TextView) rootView.findViewById(R.id.empty);
        list.setEmptyView(emptytext);

        loadingIndicator = rootView.findViewById(R.id.loading);

        adapter = new wordadapter(getActivity(), new ArrayList<word>());

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                word alink = adapter.getItem(position);
                Intent a = new Intent(getActivity(),Webviewnews.class);
                a.putExtra("url",alink.getMurl());
                startActivity(a);
            }
        });

        referesh();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadingIndicator.setVisibility(View.VISIBLE);
                emptytext.setVisibility(View.INVISIBLE);

                referesh1();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    public void referesh()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager task = getLoaderManager();
            task.initLoader(1, null, this);

        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptytext.setText("No Internet Connection");
            emptytext.setVisibility(View.VISIBLE);
        }
    }
    public void referesh1()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager task = getLoaderManager();
            task.restartLoader(1, null, this);

        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptytext.setText("No Internet Connection");
            emptytext.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public Loader<List<word>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLOADER CALLED");
        return new newsloader(getContext(), USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<word>> loader, List<word> data) {
        View loadingIndicator = getView().findViewById(R.id.loading);
        loadingIndicator.setVisibility(View.GONE);
        emptytext.setText("NO NEWS FOUND");
        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
        if(index != -1)
        {
            list.setSelectionFromTop( index, top);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<word>> loader) {
        adapter.clear();
    }
    @Override
    public void onPause() {
        super.onPause();
        index =list.getFirstVisiblePosition();
        View v = list.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index",index);
        outState.putInt("top",top);
    }

}
