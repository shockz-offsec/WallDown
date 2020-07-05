package com.example.walldown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class Singleview2 extends AppCompatActivity {

    ImageView imageView;
    SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        // Selected image id
        final int position = i.getExtras().getInt("id2");
        final ImageAdapter2 imageAdapter2 = new ImageAdapter2(this);
        preferencias = getPreferences(Activity.MODE_PRIVATE);

        setContentView(R.layout.activity_singleview);
        imageView = findViewById(R.id.singleview);
        imageView.setImageResource(imageAdapter2.quotes[position]);
        this.registerForContextMenu(imageView);


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
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        this.getMenuInflater().inflate(R.menu.menu_singleview, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean toret = false;

        Intent i = getIntent();
        final int position = i.getExtras().getInt("id2");
        final ImageAdapter2 imageAdapter2 = new ImageAdapter2(this);

        switch (item.getItemId()) {

            case R.id.fav:

                if (Favoritos.getInstance().esFavorito(imageAdapter2.quotes[position])) {
                    Favoritos.getInstance().deleteFav(imageAdapter2.quotes[position]);
                    Toast.makeText(Singleview2.this, "Se ha quitado de Favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Favoritos.getInstance().addFav(imageAdapter2.quotes[position]);
                    Toast.makeText(Singleview2.this, "Se ha añadido a Favoritos", Toast.LENGTH_SHORT).show();
                }
                toret = true;
                break;


        }
        return toret;

    }


    public void onCreateContextMenu(ContextMenu contxt, View v, ContextMenu.ContextMenuInfo cmi) {
        super.onCreateContextMenu(contxt, v, cmi);
        this.getMenuInflater().inflate(R.menu.context_menu, contxt);

    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        boolean toret = false;

        Intent i = getIntent();
        final int position = i.getExtras().getInt("id2");
        final ImageAdapter2 imageAdapter2 = new ImageAdapter2(this);


        switch (menuItem.getItemId()) {
            case R.id.establecer:

                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperManager.setResource(imageAdapter2.quotes[position]);
                    Toast.makeText(Singleview2.this, "Wallpaper Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Singleview2.this, "Fallo al actualizar el fondo de pantalla", Toast.LENGTH_SHORT).show();
                }
                toret = true;
                break;

            case R.id.compartir:

                Bitmap b = BitmapFactory.decodeResource(getResources(), imageAdapter2.quotes[position]);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        b, "Title", null);
                Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(share, "Select"));

                toret = true;
                break;


            case R.id.descargar:
                if (isExternalStorageWritable()) {
                    Bitmap d = BitmapFactory.decodeResource(getResources(), imageAdapter2.quotes[position]);
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/WallDown_images");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fname = "Image-" + n + ".jpg";
                    File file = new File(myDir, fname);
                    if (file.exists()) file.delete();

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
                        d.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                        Toast.makeText(Singleview2.this, "Wallpaper Descargado Correctamente", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(Singleview2.this, "Fallo al descargar", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Singleview2.this, "Habilita los permisos de almacenamiento externo", Toast.LENGTH_SHORT).show();
                }
                toret = true;
                break;
        }
        return toret;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
