package com.android.MAndroidImage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Random;

public class MainActivity extends Activity {
    private static final String TAG = "Log-Messages";
    private final String BASEURL = "http://cg8t.com/api/v1/users//5681034041491456/";
    private final String[] imageList = {"DSC_0095.JPG", "DSC_0074.JPG", "DSC_0031.JPG", "DSC_0032.JPG", "DSC_0006.JPG", "DSC_0064.JPG", "DSC_0023.JPG", "DSC_0026.JPG", "DSC_0038.JPG"};
    private ImageView image;
    private ProgressDialog mProgressDialog;
    private long startTime = 0l;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the layout from image.xml
        setContentView(R.layout.activity_main);

        // Locate the ImageView in activity_main.xml
        image = (ImageView) findViewById(R.id.image);

        // Locate the Button in activity_main.xml
        Button button = (Button) findViewById(R.id.button);

        // Capture button click
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                Random generator = new Random();
                int i = generator.nextInt(imageList.length);
                final String imageName = BASEURL + imageList[i];

                // Execute DownloadImage AsyncTask
                new DownloadImage().execute(imageName);
            }
        });
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download the Image");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                startTime = System.currentTimeMillis();
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            image.setImageBitmap(result);
            // Close progressdialog
            mProgressDialog.dismiss();
            String duration = String.valueOf(System.currentTimeMillis() - startTime) + " ms";
            Log.i(TAG, "Image Download time : " + duration);
            Toast.makeText(getApplicationContext(), duration + " ms", Toast.LENGTH_LONG).show();

            TextView downloadTime = (TextView) findViewById(R.id.downloadTime);
            downloadTime.setTextColor(Color.RED);
            downloadTime.setText(duration);
        }
    }
}