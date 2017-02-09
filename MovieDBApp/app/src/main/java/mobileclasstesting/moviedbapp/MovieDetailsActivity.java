package mobileclasstesting.moviedbapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import MovieDB.MovieDBRequestHandler;

public class MovieDetailsActivity extends AppCompatActivity {

    private MovieDBRequestHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //adds back button to actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new MovieDBRequestHandler();

        //set title bar title
        this.setTitle(this.getIntent().getStringExtra("MovieTitle"));

        //get and set the text labels of release date and overview
        TextView releaseLabel = (TextView)findViewById(R.id.releaseLable);
        TextView overviewLabel = (TextView)findViewById(R.id.overview);

        releaseLabel.setText("Released: " + this.getIntent().getStringExtra("Released"));
        overviewLabel.setText(this.getIntent().getStringExtra("Overview"));

        //get and draw the poster, use the w500 for higher quality
        ImageView posterView = (ImageView)findViewById(R.id.posterView);
        posterView.setImageBitmap(handler.getImage(this.getIntent().getStringExtra("MoviePoster"), 500));

        //add backdrop to the background
        Bitmap backgroundBitmapRaw = handler.getImage(this.getIntent().getStringExtra("MovieBackdrop"), 500);

        //crop image so its not stretched looking
        int wGiven = backgroundBitmapRaw.getWidth();
        int hGiven = backgroundBitmapRaw.getHeight();

        int x;
        int y;
        int w;
        int h;

        if(hGiven/2 > wGiven){ //width is smallest so it must be deciding factor

            w = wGiven;
            h = wGiven*2;

            x = 0;
            y = hGiven/2 - h/2;

        }
        else{ //height is smallest so it must be deciding factor

            w = hGiven/2;
            h = hGiven;

            x = wGiven/2 - w/2;
            y = 0;

        }


        Bitmap backgroundCropped = Bitmap.createBitmap(backgroundBitmapRaw, x, y, w, h);

        Drawable background = new BitmapDrawable(getResources(), backgroundCropped);

        background.setAlpha(50); //about 20% visible

        RelativeLayout detailsLayout = (RelativeLayout)findViewById(R.id.detailsLayout);
        detailsLayout.setBackground(background);

    }

    //action listener for back button in actionbar
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
