package MovieDB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MovieDBRequestHandler {


    private String key; //this is the access key to movie DBs API
    private boolean responded; //semaphore for internet threads
    private String threadResponse;
    private Bitmap threadBitmap;

    //by default use given key
    public MovieDBRequestHandler(){

        key = "c69c4ae8012e85b7870ae0a54f00d53b";


    }

    //in case this needs to be generalized for other keys
    public MovieDBRequestHandler(String AccessKey){

        key = AccessKey;

    }



    //gets the list of movies by name, poster and release date
    public String[][] getMovieList(){

        String data[][] = null;

        boolean success = true; //assume success until proven otherwise

        //example get
        String request = "https://api.themoviedb.org/3/discover/movie?"
                + "api_key=" + key //use key here
                + "&language=en-US&sort_by=popularity.desc"
                + "&include_adult=false"
                + "&include_video=false"
                + "&page=1";



        //since internet access must be done on another thread a semaphore
        //must be used here to wait for a response
        responded = false;
        new InternetThread(request).start();

        while(!responded){

            //do nothing, wait for semaphore to indicate request happened

        }

        String result = threadResponse;

        if(result.equals("FAILED")) //request failed
            success = false;
        else {
            try {

                //must parse out relevant data using JSON lib
                JSONObject resultObject = new JSONObject(result);

                //inner array should contain all relevant data
                JSONArray innerArray = resultObject.getJSONArray("results");
                Log.i("JSON Parse", "Array Length: " + innerArray.length());

                //using inner array, parse out all relevant data
                //want title, release_date, overview and poster_path
                data = new String[innerArray.length()][5];

                //parse each movie
                for (int index = 0; index < innerArray.length(); index++) {

                    JSONObject rawMovie = innerArray.getJSONObject(index);

                    Log.i("JSON Parse", "Movie " + index + " = " + rawMovie.toString());

                    data[index][0] = rawMovie.getString("title");
                    data[index][1] = rawMovie.getString("release_date");
                    data[index][2] = rawMovie.getString("overview");
                    data[index][3] = rawMovie.getString("poster_path");
                    data[index][4] = rawMovie.getString("backdrop_path");

                }

            } catch (JSONException e) {

                success = false;

            }
        }


        //return data if successful, return failure flag otherwise
        if(success)
            return data;
        else
            return new String[][]{{"FAILED"}};

    }







    //grabs requested image based on given path and width of the image
    public Bitmap getImage(String path, int width){

        String request = "https://image.tmdb.org/t/p/w" + width + path;
        request = request.replace("\\", ""); //remove random backslash

        threadBitmap = null;

        responded = false;

        new ImageThread(request).start();

        while(!responded){

            //do nothing, wait for semaphore to indicate request happened

        }

        return threadBitmap;

    }





    //thread uses internet since its not allowed on main thread
    public class InternetThread extends Thread {

        private String request;

        public InternetThread(String r){

            request  = r;

        }


        public void run() {

            String response = "FAILED";

            //executes given request here
            try {

                URL url = new URL(request);
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                response = reader.readLine();

                //System.out.println(response);

            }
            catch(Exception e){

                //do nothing since response is assumed as FAILED

            }

            threadResponse = response;

            responded = true;

        }

    }



    //thread retrieves image from resource url
    public class ImageThread extends Thread {

        private String request;

        public ImageThread(String r){

            request  = r;

        }


        public void run() {

            //executes given request here
            try {

                URL url = new URL(request);
                threadBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            }
            catch(Exception e) {

                //do nothing since response is assumed as FAILED

            }

            responded = true;

        }

    }



}
