package com.example.eslam.android_test;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText mailBox;
    Button checkMail;
    TextView Status;
    int count=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mailBox=(EditText)findViewById(R.id.mail);
        checkMail=(Button) findViewById(R.id.checkMail);
        Status=(TextView)findViewById(R.id.status);

        checkMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mailBox.getText().toString().isEmpty())
                {
                  Toast.makeText(getApplicationContext(),"Please Enter Mail First To Check It",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(count==5)
                    {
                        Toast.makeText(getApplicationContext(),"You have Exceed the maximum try for check mail",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        count++;
                        String mail=
                                mailBox.getText().toString().replace(" ","");
                        new checkMail().execute(mail);
                    }

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class checkMail extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            String strurl="http://apilayer.net/api/check?access_key=9617f709076a99e8f48d4f2f0dfa5bcf";
            final String FORMAT_PARAM = "format";
            final String SMTP_PARAM = "smtp";
            final String MAIL_PARAM="email";
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            String JsonString=null;
            try
            {

                Uri base_url= Uri.parse(strurl)
                        .buildUpon()
                        .appendQueryParameter(MAIL_PARAM, params[0])
                        .appendQueryParameter(SMTP_PARAM, "1")
                        .appendQueryParameter(FORMAT_PARAM, "1")
                        .build();
                URL url=new URL(base_url.toString());
                Log.e("",url.toString());
                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                JsonString = buffer.toString();
                Log.v("", "JSON String: " + JsonString);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("", "Error closing stream", e);
                    }
                }
            }
            return JsonString;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            try {
                JSONObject json = new JSONObject(data);
                if(json.getString("smtp_check").equalsIgnoreCase("false"))
                {
                    Status.setText("This mail does not exist as an SMTP domain");
                }
                else
                {
                    Status.setText("This mail exists as an SMTP domain");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
