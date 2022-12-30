package com.example.iotlabb2forlabb3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLOutput;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class MainActivity extends AppCompatActivity {
    TextView txv_temp_indoor = null;
    Switch lightToggle = null;
    Button btnUpdateTemp = null;
    String outputValue = "";

    public void run (String command) {
        String hostname = "iotpi17.dsv.su.se";
        String username = "pi";
        String password = "IoT@2021";
        StringBuilder output = new StringBuilder();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try
        {
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            //reads text

            while (true){
                String line = br.readLine(); // read line
                output.append(line);

                if (line == null)
                    break;
                System.out.println(line);
            }
            outputValue = output.toString();
/*            String[] parts = outputValue.split("-");
            String part1 = parts[0];
            String part2 = parts[1];
*/
            /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
        }
        catch (IOException e)
        { e.printStackTrace(System.err);
            System.exit(2); }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txv_temp_indoor = (TextView) findViewById(R.id.indoorTempShow);
        txv_temp_indoor.setText(outputValue);
        lightToggle = (Switch) findViewById(R.id.btnToggle);
        btnUpdateTemp = (Button) findViewById(R.id.btnUpdateTemp);


/*
//light listener

        lightToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // below you write code to change switch status and action to take


                if (isChecked) {
                    //do something if checked
                    //remember to call 0 and 1 for splitting................................................................
                    new AsyncTask<Integer, Void, Void>(){
                        @Override
                        protected Void doInBackground(Integer... params) {
                            //your code to fetch results via SSH
                            run("python turnondevices.py");
                            return null;
                        }
                    }.execute(1);

                } else {
                    // to do something if not checked
                    new AsyncTask<Integer, Void, Void>(){
                        @Override
                        protected Void doInBackground(Integer... params) {
                            //your code to fetch results via SSH
                            run("python turnoffdevice.py");
                            return null;
                        }
                    }.execute(1);
                }
            }

        });
*/
        //button listener
        btnUpdateTemp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            public void onClick(View v) {
                // Add code to execute on click
                new  AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        // Add code to fetch data via SSH
                        run("python listsensorsandvalues.py");
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void v) {
                        // Add code to preform actions after doInBackground
                        txv_temp_indoor.setText(outputValue);

                    }

                }.execute(1);



            }
        });
    }
}