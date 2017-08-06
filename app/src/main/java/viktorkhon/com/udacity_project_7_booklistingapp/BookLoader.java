package viktorkhon.com.udacity_project_7_booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static viktorkhon.com.udacity_project_7_booklistingapp.MainActivity.LOG_TAG;

/**
 * Created by Viktor Khon on 8/4/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // Query URL
    private String mUrl;

    private List<Book> result;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        result = QueryUtils.fetchBookData(mUrl);
        return result;
    }

    @Override
    public void deliverResult(List<Book> data) {
        result = data;
        super.deliverResult(data);
    }
}