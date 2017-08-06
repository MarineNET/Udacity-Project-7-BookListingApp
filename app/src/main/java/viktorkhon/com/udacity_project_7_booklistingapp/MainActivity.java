package viktorkhon.com.udacity_project_7_booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter mBookAdapter;

    private ListView bookListView;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final String REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private EditText title;

    private String toSearch;

    private ProgressBar mProgressBar1;

    private TextView emptyView;

    NetworkInfo activeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.editText);

        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView = (ListView) findViewById(R.id.bookListView);

        bookListView.setAdapter(mBookAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        emptyView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mProgressBar1 = (ProgressBar) findViewById(R.id.progressBar3);

        // Class that answers queries about the state of network connectivity.
        // It also notifies applications when network connectivity changes.
        ConnectivityManager cm = (ConnectivityManager)
                // Context.CONNECTIVITY_SERVICE:
                // Use with getSystemService(Class) to retrieve a ConnectivityManager for handling
                // management of network connections.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Describes the status of a network interface.
        // Use getActiveNetworkInfo() to get an instance that represents the current network connection.
        activeNetwork = cm.getActiveNetworkInfo();

        getLoaderManager().initLoader(0, null, MainActivity.this);
        mProgressBar1.setVisibility(View.GONE);

        Button search = (Button) findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = title.getText().toString();
                String finalQuery = query.replace(" ", "+");

                toSearch = REQUEST_URL + finalQuery;
                if (isConnected()) {
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                    mProgressBar1.setVisibility(View.VISIBLE);
                } else {
                    // If no network found, display a text and hide progress bar
                    emptyView.setText("No internet connection");
                    mProgressBar1.setVisibility(View.GONE);
                }
            }
        });
    }


    final boolean isConnected() {
        // isConnectedOrConnecting - Indicates whether network connectivity exists or is
        // in the process of being established
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
        } return true;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        return new BookLoader(this, toSearch);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Clear the adapter of previous earthquake data
        mBookAdapter.clear();
        mProgressBar1.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        Log.i(LOG_TAG, "onLoadFinished for emplty view");
        emptyView.setText("No books found");

        if (data != null && !data.isEmpty()) {
            mBookAdapter.addAll(data);
            //bookListView.setAdapter(mBookAdapter);
            bookListView.setAdapter(mBookAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBookAdapter.clear();
    }
}