package com.example.musicplay;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.listview);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();

                        //Now access to external storage granted so we can now go through the external storage
                        //this is just like  the below code
                        ArrayList<File> mysongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mysongs.size()];
                        //loop through the songs in sd card stored in mysings array list and display them by their names
                        for (int i = 0; i < mysongs.size(); i++) {
                            items[i] = mysongs.get(i).getName().replace(".mp3", "");//Replace the mp3 by "" in the name
                        }//Now use adapter as input to string and output as in the form of views
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, Playsong.class);
                                String currentsong = listview.getItemAtPosition(position).toString();
                                intent.putExtra("mysongs", mysongs);//list of songs
                                intent.putExtra("postion", position);
                                startActivity(intent);

                            }
                        });

                    }


                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                })
                .check();

    }

    //Method  to get the list of songs in an arraylist from your phones storage
    public ArrayList<File> fetchSongs(File file) {//File is a directory or location of sd card storage
        //  fetch songs is  a function which takes file(url) as an argument input and stores it in an ArrayList named file
        ArrayList arrayList = new ArrayList();
        //get all the songs in file and search them in directory to place them in the arrayList
        File[] songs = file.listFiles();//list all song files in an array list  file is a directory
        //File has all the song files
        //songs has all the directory names in file directory
        if (songs != null) {
            for (File myFile : songs) {
                //myfile is also a directory which is checked inside the current songs folder
                //if myfile has more files then loop through all of them
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchSongs(myFile));
                }
                else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        // to not play files of an application data base
                        arrayList.add(myFile);

                    }
                }
            }
        }
        return arrayList;//returns directory of all files in the form of an arrayList




    }


}