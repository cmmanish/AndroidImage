package com.android.MAndroidImage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "Log-Messages";
    private final String url = "http://cg8t.com/api/v1/users//5681034041491456/";
    private final String[] imageList = {"DSC_0095.JPG", "DSC_0074.JPG", "DSC_0031.JPG", "DSC_0032.JPG", "DSC_0006.JPG", "DSC_0064.JPG", "DSC_0023.JPG", "DSC_0026.JPG", "DSC_0038.JPG"};
    private File photoFile = new File("");
    String timeStamp = new SimpleDateFormat("mmdd").format(new Date());
    private ImageView image;
    private ProgressDialog mProgressDialog;
    private long startTime = 0l;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("CheckStartActivity", "onActivityResult and resultCode = " + resultCode);
        // TODO Auto-generated method stub
        putPhoto(photoFile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Get the layout from image.xml
        setContentView(R.layout.activity_main);
        // Locate the ImageView in activity_main.xml
        image = (ImageView) findViewById(R.id.image);
        // Locate the Button in activity_main.xml
        Button button = (Button) findViewById(R.id.buttonDownload);
        Button memcacheButton = (Button) findViewById(R.id.memcacheButton);

        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        // Capture button click
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Random generator = new Random();
                int i = generator.nextInt(imageList.length);
                final String imageName = url + imageList[i];
                // Execute DownloadImage AsyncTask
                new DownloadImage().execute(imageName);
            }
        });

        cameraButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                photoFile = dispatchTakePictureIntent();
            }
        });

        memcacheButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                Random generator = new Random();
                int i = generator.nextInt(imageList.length);

                final String imageName = url + imageList[i];
                // Execute DownloadImage AsyncTask
                new DownloadImage().execute(imageName);

                new DownloadImage().execute(imageName);
            }

        });

    }

    private File dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //     File photoFile = null;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "Exception thrown in dispatchTakePictureIntent()" + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        return photoFile;
    }

    public void putPhoto(File photoFile) {

        try {
            String fileName = photoFile.getName();
            Log.i(TAG, "FileName: " + fileName);
            String serverResponse;
            HttpParams params = new BasicHttpParams();
            params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            HttpPut put = new HttpPut(url + "/" + fileName);
            FileEntity fileEntity = new FileEntity(photoFile, "image/jpeg");
            put.setEntity(fileEntity);

            startTime = System.currentTimeMillis();
            HttpResponse response = client.execute(put);
            String duration = String.valueOf(System.currentTimeMillis() - startTime);

            long fileSize = photoFile.length();
            Log.i(TAG, "Upload fileSize: " + fileSize);

            String speed = String.valueOf(fileSize / Integer.parseInt(duration));

            Log.i(TAG, response.getStatusLine().toString());
            //            Toast.makeText(getApplicationContext(), duration, Toast.LENGTH_LONG).show();

            TextView downloadTime = (TextView) findViewById(R.id.Time);
            TextView networkspeed = (TextView) findViewById(R.id.networkSpeed);

            networkspeed.setTextColor(Color.BLUE);
            networkspeed.setText(speed + " kbps");

            downloadTime.setTextColor(Color.RED);
            downloadTime.setText(duration + " ms");

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                serverResponse = EntityUtils.toString(entity);
                Log.i(TAG, (serverResponse));
            }
        } catch (Exception e) {
            Log.i(TAG, "Error in PUTPhotoTOServer" + e.getMessage());
        }
    }

    // Todo: Show thumbnail
    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        Log.i(TAG,"result code: " + resultCode + " and request code: " + requestCode);
    //        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
    //            Bundle extras = data.getExtras();
    //            Bitmap imageBitmap = (Bitmap) extras.get("data");
    //            image.setImageBitmap(imageBitmap);
    //        }
    //        else
    //            Log.i(TAG,"NO");
    //    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File file = new File("/storage/emulated/legacy/Pictures");
        String imageFileName = "FromAndroid_";
        File image = File.createTempFile(imageFileName, ".jpg", file);
        return image;
    }

    /////////////////////////////////////////////////////////////////
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
            TextView networkSpeed = (TextView) findViewById(R.id.networkSpeed);
            networkSpeed.setText("");
            TextView downloadTime = (TextView) findViewById(R.id.Time);
            downloadTime.setText("");
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
            String duration = String.valueOf(System.currentTimeMillis() - startTime);
            Log.i(TAG, "Image Download time : " + duration);
            //            Toast.makeText(getApplicationContext(), duration + " ms", Toast.LENGTH_LONG).show();
            TextView downloadTime = (TextView) findViewById(R.id.Time);
            downloadTime.setTextColor(Color.RED);
            downloadTime.setText(duration + " ms");

            long fileSize = (result.getWidth() * result.getHeight());

            Log.i(TAG, "Download fileSize: " + fileSize / 1024);
            String speed = String.valueOf(fileSize / Integer.parseInt(duration));

            TextView networkSpeed = (TextView) findViewById(R.id.networkSpeed);
            networkSpeed.setTextColor(Color.BLUE);
            networkSpeed.setText(speed + " kbps");
            Log.i(TAG, "Network Speed : " + speed);

        }
    }
}