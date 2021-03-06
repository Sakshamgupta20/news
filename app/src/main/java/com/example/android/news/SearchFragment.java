package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saksham on 23-01-2018.
 */

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<word>> {
    private wordadapter adapter = null;
    private TextView emptytext;

    private EditText search;
    String a;
    String modified;
    public static int index = -1;
    public static int top = -1;
    ListView list;
    private Button retry;
    public static final String LOG_TAG = utils.class.getSimpleName();

     String USGS_REQUEST_URL = "https://newsapi.org/v2/everything";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.search,container,false);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh1);

         list = (ListView) rootView.findViewById(R.id.list1);

        search=(EditText)rootView.findViewById(R.id.searchedit);

        if(savedInstanceState!=null) {
            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
            modified=savedInstanceState.getString("modi");
        }
        emptytext = (TextView) rootView.findViewById(R.id.empty1);
        list.setEmptyView(emptytext);

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


        Button sbutton=(Button)rootView.findViewById(R.id.sbutton);

        sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                 a=search.getText().toString();
                  modified=USGS_REQUEST_URL+"?q="+a+"&apiKey=b5bfe33e18c943d7bc7a757b1760160c+"+"&sortBy=publishedAt";

                  search();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                a=search.getText().toString();
                modified=USGS_REQUEST_URL+"?q="+a+"&apiKey=b5bfe33e18c943d7bc7a757b1760160c+"+"&sortBy=publishedAt";
                adapter = new wordadapter(getActivity(), new ArrayList<word>());
                list.setAdapter(adapter);
                View loadingIndicator = getView().findViewById(R.id.loading1);
                loadingIndicator.setVisibility(View.VISIBLE);
                emptytext.setVisibility(View.INVISIBLE);

                search();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        if(!TextUtils.isEmpty(modified))
        {
            search();
        }
        return rootView;
    }

public void search()
{
    ConnectivityManager cm =
            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    final boolean isConnected = activeNetwork != null &&
            activeNetwork.isConnectedOrConnecting();
    View loadingIndicator = getActivity().findViewById(R.id.loading1);
    if (isConnected) {
        LoaderManager task = getLoaderManager();
        task.restartLoader(1, null,SearchFragment.this);
    } else {
        loadingIndicator.setVisibility(View.GONE);
        emptytext.setText("No Internet Connection");
        emptytext.setVisibility(View.VISIBLE);
    }
}
    @Override
    public Loader<List<word>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLOADER CALLED");
        emptytext.setVisibility(View.INVISIBLE);
       // Uri baseUri = Uri.parse(USGS_REQUEST_URL);
       // Uri.Builder uriBuilder = baseUri.buildUpon();
       // uriBuilder.appendQueryParameter("q", a);
        //uriBuilder.appendQueryParameter("apiKey","b5bfe33e18c943d7bc7a757b1760160c");
        return new newsloader(getContext(),modified);
    }

    @Override
    public void onLoadFinished(Loader<List<word>> loader, List<word> data) {
        View loadingIndicator = getView().findViewById(R.id.loading1);
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
        outState.putString("modi",modified);
    }

}
