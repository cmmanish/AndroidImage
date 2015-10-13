package com.android.MAndroidImage;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmadhusoodan on 10/13/15.
 */
public class httpMethods {

    private static final String TAG = "Log-Messages";

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
}
