package mobileclasstesting.moviedbapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

import MovieDB.MovieDBRequestHandler;

public class MainActivity extends AppCompatActivity {

    private MovieDBRequestHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieves list of movies from moviesDB.com API
        handler = new MovieDBRequestHandler(); //test with param "failKey" to test failed connection
        String data[][] = handler.getMovieList();

        if(!data[0][0].equals("FAILED")) {

            ArrayList<Movie> items = new ArrayList<Movie>();
            for (int index = 0; index < data.length; index++) {

                Log.i("Adding Movie", "Movie = " + data[index][0]); //for testing

                Movie movie = new Movie(data[index][0], data[index][1],
                        data[index][2], data[index][3], data[index][4]);

                items.add(movie);

            }

            movieAdapter adapter = new movieAdapter(this, items);


            ListView mainList = (ListView) findViewById(R.id.mainList);
            mainList.setAdapter(adapter);
            mainList.setOnItemClickListener(new movieClickListener());

        }
        else{

            this.setTitle("Connection Failed!");

        }

    }





    //class that stores individual movie data
    private class Movie{

        private String title;
        private String releaseDate;
        private String overview;
        private String posterPath;
        private String backdropPath;

        public Movie(String movieTitle, String movieRelease,
                     String over, String poster, String backdrop){

            title = movieTitle;
            releaseDate = movieRelease;
            overview = over;
            posterPath = poster;
            backdropPath = backdrop;

        }

        public String getTitle(){

            return title;

        }

        public String getReleaseDate(){

            return releaseDate;

        }

        public String getOverview(){

            return overview;

        }

        public String getPosterPath(){

            return posterPath;

        }


        public String getbackdropPath(){

            return backdropPath;

        }

    }



    //uses the custom made xml layout to properly adapt movie to listView
    public class movieAdapter extends ArrayAdapter<Movie>{

        public movieAdapter(Context context, ArrayList<Movie> movies){

            super(context, 0, movies);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            Movie movie = getItem(position);

            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);

            TextView titleLabel = (TextView)convertView.findViewById(R.id.title);
            TextView releaseLabel = (TextView)convertView.findViewById(R.id.release);

            ImageView posterImage = (ImageView)convertView.findViewById(R.id.poster_image);

            //get the w92 image since its smaller and will load faster
            posterImage.setImageBitmap(handler.getImage(movie.getPosterPath(), 92));

            titleLabel.setText(movie.getTitle());
            releaseLabel.setText("Released: " + movie.getReleaseDate());

            return convertView;

        }

    }







    private class movieClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position, long var){

            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);

            Movie movie = (Movie)adapter.getItemAtPosition(position);

            intent.putExtra("MovieTitle", movie.getTitle());
            intent.putExtra("Released", movie.getReleaseDate());
            intent.putExtra("Overview", movie.getOverview());
            intent.putExtra("MoviePoster", movie.getPosterPath());
            intent.putExtra("MovieBackdrop", movie.getbackdropPath());

            startActivity(intent);

        }



    }

}
