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
                Log.e(LOG_TAG, "Status code is " + urlConnection.getResponseCode());
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
            // Main JSON object from the response
            JSONObject jsonObject = new JSONObject(bookJSON);

            // Parsed array of "items"
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                // Go through each index of an array to extract appropriate values of objects
                JSONObject item = items.getJSONObject(i);
                // "volumeInfo is part of array "item" that has additional objects that we need
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                // Check if "authors" field exist in "volumeInfo" object, if so get a JSONArray
                // Go through each index and store each author's name in a String variable
                // If no such field exists, use default value
                String author = "";
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < authors.length(); j++) {
                        author = authors.getString(j) + " " + author;
                    }
                } else {
                    author = "No author available";
                }

                // If no publisher available, use default text
                String publisher;
                if (volumeInfo.has("publisher")) {
                    publisher = volumeInfo.getString("publisher");
                } else {
                    publisher = "No publisher available";
                }

                // Check if "listPrice" exists in "saleInfo" JSONObject, if so get "amount"
                // If not use default text
                JSONObject saleInfo = item.getJSONObject("saleInfo");
                String price;
                if (saleInfo.has("listPrice")) {
                    JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                    price = "$" + listPrice.getString("amount");
                    ;
                } else {
                    price = "No price";
                }

                // Check if "webReaderLink" exists in "accessInfo" JSONObject, if so get its value
                // If not, set link to a random website
                JSONObject accessInfo = item.getJSONObject("accessInfo");
                String link;
                if (accessInfo.has("webReaderLink")) {
                    link = accessInfo.getString("webReaderLink");
                } else {
                    link = "google.com";
                }

                // Create new Book object with approriate parameters
                Book book = new Book(title, author, price, publisher, link);
                // Add Book objects to an ArrayList
                books.add(book);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        return books;
    }

    public static List<Book> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get a query url as a String and cast it as an URL class
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            // Use the new URL to make an Http request to API server for Google Books, receive
            // back a JSON string with requested values
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Parse the JSON list and store it as a List<Book> object and return it to the UI
        return parseJsonData(jsonResponse);
    }
}