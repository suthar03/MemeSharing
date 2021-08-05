package com.suthar.memesharing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
ImageView ivMeme;
ProgressBar prBar;
Button btnShare;
String MemeUrl =null;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivMeme = findViewById(R.id.imageView);
        prBar = findViewById(R.id.pbBar);
        btnShare = findViewById(R.id.btnShare);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        loadMeme();

        prBar.setVisibility(View.GONE);


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Error:00","Button clicked");
                /*BitmapDrawable drawable = (BitmapDrawable) ivMeme.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                File filepath= Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath()+"/Demo/");
                Log.d("Error:01",dir.getAbsolutePath());
                dir.mkdir();
                File file = new File(dir,System.currentTimeMillis()+".jpg");

                try {
                    Toast.makeText(MainActivity.this,"InSide Try ",Toast.LENGTH_SHORT).show();
                    outputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    Log.d("Error:1",e.getMessage());
                    e.printStackTrace();

                }
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                Toast.makeText(MainActivity.this,"Saved Successfully "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    Log.d("Error:2",e.getMessage());
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d("Error:3",e.getMessage());
                    e.printStackTrace();
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(share,"Share via"));*/


                BitmapDrawable drawable = (BitmapDrawable) ivMeme.getDrawable();
                Bitmap b = drawable.getBitmap();
                //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.refer_pic);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        b, "MEME", "Just A MEME");
                Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                share.putExtra(Intent.EXTRA_TEXT, "Shared via MemeSharing App");
                startActivity(Intent.createChooser(share, "Share this Meme using"));

            }
        });

    }
private void loadMeme(){
    //final TextView textView = (TextView) findViewById(R.id.text);
// ...

// Instantiate the RequestQueue.

    String url ="https://meme-api.herokuapp.com/gimme";
    prBar.setVisibility(View.VISIBLE);
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("SuccessMsg","Response: " + response.toString()+"\n");
                    try {
                        MemeUrl = response.getString("url");
                        Glide .with(MainActivity.this).load(MemeUrl).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                prBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this,"We are facing some difficulties",Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                prBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(ivMeme);
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.d("ErrorMsg","Error: " + error.toString()+"\n");

                }
            });

// Access the RequestQueue through your singleton class.
    MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);


}
    public void ShareMeme(View view) {
        //MemeUrl
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, this is one of best Meme "+MemeUrl);
        intent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(intent, "Choose anyone to share this meme...");
        startActivity(shareIntent);
    }

    public void NextMeme(View view) {
        loadMeme();
    }


    public void ShareMemeIV(View view) {
        View content = ivMeme;
        content.setDrawingCacheEnabled(true);

        Bitmap bitmap = content.getDrawingCache();
        File root = Environment.getExternalStorageDirectory();
        File dir  = new File(root.getAbsolutePath() + "/Camera/");
        dir.mkdirs();
        File cachePath = new File(root.getAbsolutePath() + "/Camera/image.jpg");
        Log.d("ImageLocation:",cachePath.getAbsolutePath()+"\n");
        try {
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,"Failed to send ",Toast.LENGTH_SHORT).show();
            Log.d("ImageError:",e.getMessage()+"\n");
            //e.printStackTrace();
        }

        Log.d("ImageCreatedOn:",cachePath.getAbsolutePath()+"\n");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
        startActivity(Intent.createChooser(share,"Share via"));
    }

    public void ShareMemeIVmg(View view){
        View content = ivMeme;
        content.setDrawingCacheEnabled(true);
        Bitmap bit = BitmapFactory.decodeResource(this.getResources(),  R.drawable.meme);
        File filesDir = this.getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, "birds.png");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bit.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, imageFile);

        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        startActivity(intent);
    }



}
