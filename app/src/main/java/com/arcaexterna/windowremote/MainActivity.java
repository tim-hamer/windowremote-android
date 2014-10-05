package com.arcaexterna.windowremote;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity {

    String authToken = "DEnUBEfapUca3eST5ZacH33r2phuva5a";
    AsyncHttpClient myClient;
    String hostname;
    String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClient = new AsyncHttpClient();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("default", Context.MODE_PRIVATE);
        String savedHostname = preferences.getString(getString(R.string.hostname), "");
        String savedPort = preferences.getString(getString(R.string.port), "5000");

        ((EditText)findViewById(R.id.hostnameField)).setText(savedHostname);
        ((EditText)findViewById(R.id.portField)).setText(savedPort);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openButtonPressed(View view) {
        pressButton("open");
    }

    public void closeButtonPressed(View view) {
        pressButton("close");
    }

    private void pressButton(String command) {
        if (validateSettings()) {
            try {
                makeHttpRequestWithCommand(command);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Settings", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateSettings() {
        hostname = ((EditText)findViewById(R.id.hostnameField)).getText().toString();
        port = ((EditText)findViewById(R.id.portField)).getText().toString();
        if (port == null || port.length() == 0) {
            port = "5000";
        }

        if(hostname != null && hostname.length() > 0) {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("default", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.hostname), hostname);
            editor.putString(getString(R.string.port), port);
            editor.commit();
            return true;
        }

        return false;
    }

    private void makeHttpRequestWithCommand(String command)  throws UnsupportedEncodingException {
        String jsonString = "{\"token\":\"" + authToken + "\"}" ;
        HttpEntity httpEntity = new StringEntity(jsonString);
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Content-Type", "application/json");

        myClient.post(this, "http://" + hostname + ":" + port + "/command/" + command, headers,
                httpEntity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
