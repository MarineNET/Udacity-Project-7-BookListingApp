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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter mBookAdapter;

    private ListView bookListView;

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final String JSON_RESPONSE =
            "https://www.googleapis.com/books/v1/volumes?q=";

    EditText title;

    private String searchForText;

    private String toSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.editText);


        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView = (ListView) findViewById(R.id.bookListView);

        bookListView.setAdapter(mBookAdapter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Button search = (Button) findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchForText = title.getText().toString().trim();
                toSearch = JSON_RESPONSE + searchForText;
                Log.i(LOG_TAG, "This is initLoader");
                if (isConnected) {
                    getLoaderManager().initLoader(0, null, MainActivity.this);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "This is onCreateLoader");
        return new BookLoader(this, toSearch);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        if (data != null && !data.isEmpty()) {
            Log.i(LOG_TAG, "This is onLoadFinished");
            mBookAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "This is onLoaderReset");
        mBookAdapter.clear();
    }
}
