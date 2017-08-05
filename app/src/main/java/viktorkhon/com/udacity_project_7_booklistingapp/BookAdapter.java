package viktorkhon.com.udacity_project_7_booklistingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Viktor Khon on 8/3/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);

            final Book book = getItem(position);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(book.getTitle());

            TextView authors = (TextView) convertView.findViewById(R.id.authors);
            authors.setText(book.getAuthors());

            TextView publisher = (TextView) convertView.findViewById(R.id.publisher);
            publisher.setText(book.getPublisher());

            TextView price = (TextView) convertView.findViewById(R.id.price);
            price.setText(book.getPrice());

            Button sample = (Button) convertView.findViewById(R.id.sample_url);
            sample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri webpage = Uri.parse(book.getStringUrl());
                    // Create a new intent to view the earthquake URI
                    Intent openBrowser = new Intent(Intent.ACTION_VIEW, webpage);
                    // Send the intent to launch a new activity
                    getContext().startActivity(openBrowser);
                }
            });
        }
        return convertView;
    }
}