package viktorkhon.com.udacity_project_7_booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static viktorkhon.com.udacity_project_7_booklistingapp.MainActivity.LOG_TAG;

/**
 * Created by Viktor Khon on 8/3/2017.
 */

public final class QueryUtils {

    static Book book;

    private QueryUtils() {
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Status code is  " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException is thrown");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            // This lets inputStreamReader know what we want the data to be translated to
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            // BufferedReader class makes it possible to take bytes of information from the inputStreamReader
            // and combine them in larger readable texts that we can use
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Read 1 line of text at the time and store it in a String variable
            String line = reader.readLine();
            // While there are still lines, continue this loop
            while (line != null) {
                // Use StringBuilder class to take 1 line at a time and then add new line to the
                // existing text
                output.append(line);
                // get the next line, until run out of lines
                line = reader.readLine();
            }
        }
        // return a full JSON String
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Book> parseJsonData(String bookJSON) {
        // If there is no JSON being passed, quit
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(bookJSON);

            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONObject saleInfo = item.getJSONObject("saleInfo");
                JSONObject accessInfo = item.getJSONObject("accessInfo");

                String title = volumeInfo.getString("title");
                String publisher = volumeInfo.getString("publisher");
                JSONObject listPrice;
                String price;
                String link;
                URL url;

                String author = "";
                JSONArray authors = volumeInfo.getJSONArray("authors");
                for (int j = 0; j < authors.length(); j++) {
                    author = authors.getString(j) + " " + author;
                }

                if (saleInfo.has("listPrice")) {
                    listPrice = saleInfo.getJSONObject("listPrice");
                    price = "$"+ listPrice.getString("amount");;
                } else {
                    price = "No price";
                }

                if (accessInfo.has("webReaderLink")) {
                    link = accessInfo.getString("webReaderLink");
                } else {
                    link = "";
                }

                book = new Book(title, author, price, publisher, link);
                books.add(book);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        return books;
    }

    public static List<Book> fetchBookData(String requestUrl) {
        Log.i(LOG_TAG, "This is fetchBookData");
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return parseJsonData(jsonResponse);
    }
}
