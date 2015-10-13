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
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "Log-Messages";
    private final String BASEURL = "http://cg8t.com/api/v1/users//5681034041491456/";
    private final String[] imageList = {"DSC_0095.JPG", "DSC_0074.JPG", "DSC_0031.JPG", "DSC_0032.JPG", "DSC_0006.JPG", "DSC_0064.JPG", "DSC_0023.JPG", "DSC_0026.JPG", "DSC_0038.JPG"};
    private String mCurrentPhotoPath;
    private File photoFile = new File("");
    private ImageView image;
    private ProgressDialog mProgressDialog;
    private long startTime = 0l;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("CheckStartActivity", "onActivityResult and resultCode = " + resultCode);
        // TODO Auto-generated method stub
        //        postPhoto(photoFile);

        //        POSTPhotoTOServer(photoFile);
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

        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);

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

        cameraButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                photoFile = dispatchTakePictureIntent();
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
                //        postPhoto(photoFile);
            }
        }
        return photoFile;
    }

    public void putPhoto(File photoFile) {

        try {
            String fileName = photoFile.getName();
            Log.i(TAG, "FileName: " + fileName);

            String serverResponse = null;
            HttpParams params = new BasicHttpParams();
            params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            String url = "http://cg8t.com/api/v1/users/5681034041491456/";
            HttpPut put = new HttpPut(url + "/" + fileName);

            FileEntity fileEntity = new FileEntity(photoFile, "image/jpeg");
            put.setEntity(fileEntity);

            startTime = System.currentTimeMillis();
            HttpResponse response = client.execute(put);

            String duration = String.valueOf(System.currentTimeMillis() - startTime) + " ms";

            Log.i(TAG, response.getStatusLine().toString());
            Toast.makeText(getApplicationContext(), duration, Toast.LENGTH_LONG).show();

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                serverResponse = EntityUtils.toString(entity);
                Log.i(TAG, (serverResponse));
            }
        } catch (Exception e) {
            Log.i(TAG, "Error in POSTPhotoTOServer" + e.getMessage());
        }
    }

    // HTTP POST request
    public void POSTPhotoTOServer(File photoFile) {
        String url = "http://cg8t.com/api/v1/users/5681034041491456/";
        String USER_AGENT = "Mozilla/5.0";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(url);

        try {
            List<NameValuePair> urlParameters = new ArrayList<>();// you have pass, invnum and image
            urlParameters.add(new BasicNameValuePair("image", photoFile.toString()));
            urlParameters.add(new BasicNameValuePair("User-Agent", USER_AGENT));
            urlParameters.add(new BasicNameValuePair("Accept-Language", "en-US,en;q=0.5"));

            postRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
            httpClient.execute(postRequest);

            HttpResponse response = httpClient.execute(postRequest);

            Log.i(TAG, "\nSending 'POST' request to URL : " + url);
            Log.i(TAG, "Post parameters : " + urlParameters);
            Log.i(TAG, "Response Code : " + response.getStatusLine().getStatusCode());

            // Read the response
            String jsonString = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "response after upload" + jsonString);

        } catch (Exception e) {
            Log.i(TAG, "Error in POSTPhotoTOServer" + e.getMessage());
        }
    }

    private void postPhoto(File photoFile) {
        String url = "http://cg8t.com/api/v1/users/5681034041491456/blah.jpg";
        Log.i(TAG, "url: " + url + " photoFile path: " + photoFile.getAbsolutePath());
        String USER_AGENT = "Mozilla/5.0";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            BufferedReader reader = null;
            String urlParameters = "limit=1";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(photoFile)));
            for (String line; (line = reader.readLine()) != null; ) {
                wr.writeBytes(line);
            }
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            Log.i(TAG, "\nSending 'POST' request to URL : " + url);
            Log.i(TAG, "Post parameters : " + urlParameters);
            Log.i(TAG, "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Log.i(TAG, response.toString());

            in.close();
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e.toString());
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
        File f = new File("/storage/emulated/legacy/Pictures");
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "FromAndroid_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", f);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file: " + image.getAbsolutePath();
        return image;
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