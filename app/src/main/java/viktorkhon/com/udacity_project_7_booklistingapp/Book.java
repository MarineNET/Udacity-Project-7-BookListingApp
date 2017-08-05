package viktorkhon.com.udacity_project_7_booklistingapp;

/**
 * Created by Viktor Khon on 8/3/2017.
 */

public class Book {

    private String mTitle;

    private String mAuthors;

    private String mPrice;

    private String mPublisher;

    private String mUrl;

    public Book(String title, String authors, String price, String publisher, String url) {
        mTitle = title;
        mAuthors = authors;
        mPrice = price;
        mPublisher = publisher;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getStringUrl() {
        return mUrl;
    }
}
