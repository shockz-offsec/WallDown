package com.example.walldown;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity {
    Button b1, b2, b3, b4, b5, exit;
    TextView textTargetUri;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencias = getPreferences(Activity.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        b1 = findViewById(R.id.t1);
        b2 = findViewById(R.id.t2);
        b3 = findViewById(R.id.t3);
        b4 = findViewById(R.id.t4);
        b5 = findViewById(R.id.t5);
        exit = findViewById(R.id.exit);
        textTargetUri = findViewById(R.id.targeturi);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Paisajes.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Frases.class);
                startActivity(i);

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Animales.class);
                startActivity(i);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Anime.class);
                startActivity(i);
            }
        });
        //para album del usuario
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        checkPermission();
    }


    @Override
    public void onResume() {
        super.onResume();
        Favoritos.importarConjunto(preferencias.getStringSet("favoritos", new HashSet<String>()));
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putStringSet("favoritos", Favoritos.exportarConjunto());
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(MainActivity.this, "Wallpaper Actualizado Correctamente", Toast.LENGTH_LONG).show();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean toret = false;
        switch (item.getItemId()) {
            case R.id.InfoItem:
                Intent i = new Intent(MainActivity.this, Info.class);
                startActivity(i);
                toret = true;
                break;

            case R.id.version:
                Intent i1 = new Intent(MainActivity.this, Version.class);
                startActivity(i1);
                toret = true;
                break;

        }
        return toret;

    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);

                Toast.makeText(this, "Requesting permissions", Toast.LENGTH_SHORT).show();

            } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_SHORT).show();

            }

        }

        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK Permissions granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}

