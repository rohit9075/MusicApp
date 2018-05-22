package com.example.rohit.musicapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;


public class ListOfSongsActivity extends AppCompatActivity {

    ListView mListView;
    Button mButtonStop;
    TextView mTextViewSongName;
    CardView mCardView;
    ArrayList<SongObject> mListofContent;
    AdapterClass adapter;
    String PATH;
    static String mAbsolutePath, mSongName;
    public static boolean playing = false;

    void initViews() {
        //initializing views
        mButtonStop = (Button) findViewById(R.id.btnPlayStop);
        mTextViewSongName = (TextView) findViewById(R.id.txtSongName);
        mCardView = (CardView) findViewById(R.id.cardView);
        mListView = (ListView) findViewById(R.id.listView);
        mListofContent = new ArrayList<>();

        //If music is playing already on opening starting the app, player should be visible with Stop button
        if (playing) {
            mTextViewSongName.setText(mSongName);
            mCardView.setVisibility(View.VISIBLE);
            mButtonStop.setText("Stop");
        }

        //Gives you the full PATH of phone memory
        PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

        //Calling the function which fetches the mList of music files
        initList(PATH);

        //initializing the adapter and passing the context, mList item and mList of references of SongObject
        adapter = new AdapterClass(this, R.layout.list_item, mListofContent);
        mListView.setAdapter(adapter);

        //handling events when user clicks on any music file in mList view
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //player is visible
                mCardView.setVisibility(View.VISIBLE);

                //If some other song is already playing, stop the service
                if (playing) {
                    Intent i = new Intent(ListOfSongsActivity.this, MusicService.class);
                    stopService(i);
                }

                playing = true;

                //getting absolute PATH of selected song from bean class 'SongObject'
                SongObject sdOb = mListofContent.get(position);
                mAbsolutePath = sdOb.getmAbsolutePath();

                //Play the selected song by starting the service
                Intent start = new Intent(ListOfSongsActivity.this, MusicService.class);
                startService(start);

                //Get and set the name of song in the player
                mSongName = mListofContent.get(position).getmFileName();
                mTextViewSongName.setText(mSongName);
                mButtonStop.setText("Stop");
            }

        });

        //Handling events when button Play/Stop is clicked in the player
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    //If song is playing and user clicks on Stop button
                    //Stop the song by calling stopService() and change boolean value
                    //text on button should be changed to 'Play'
                    playing = false;
                    mButtonStop.setText(R.string.text_play);
                    Intent i = new Intent(ListOfSongsActivity.this, MusicService.class);
                    stopService(i);
                } else if (!playing) {
                    //If song is not playing and user clicks on Play button
                    //Start the song by calling startService() and change boolean value
                    //text on button should be changed to 'Stop'
                    playing = true;
                    mButtonStop.setText(R.string.text_stope);
                    Intent i = new Intent(ListOfSongsActivity.this, MusicService.class);
                    startService(i);
                }
            }
        });

    }

    //Fetching .mp3 and .mp4 files from phone storage
    void initList(String path) {
        try {
            File file = new File(path);
            File[] filesArray = file.listFiles();
            String fileName;
            for (File file1 : filesArray) {
                if (file1.isDirectory()) {
                    initList(file1.getAbsolutePath());
                } else {
                    fileName = file1.getName();
                    if ((fileName.endsWith(".mp3")) || (fileName.endsWith(".mp4"))) {
                        mListofContent.add(new SongObject(file1.getName(), file1.getAbsolutePath()));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Handling permissions for Android Marshmallow and above
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission granted, initialize the views
            initViews();
        } else {
            //show the dialog requesting to grant permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    //permission is denied (this is the first time, when "never ask again" is not checked)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    //permission is denied (and never ask again is  checked)
                    else {
                        //shows the dialog describing the importance of permission, so that user should grant
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You have forcefully denied Read storage permission.\n\nThis is necessary for the working of app." + "\n\n" + "Click on 'Grant' to grant permission")
                                //This will open app information where user can manually grant requested permission
                                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                //close the app
                                .setNegativeButton("Don't", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);

        // If Android Marshmallow or above, then check if permission is granted
        if (Build.VERSION.SDK_INT >= 23)
            checkPermission();
        else
            initViews();
    }
}



