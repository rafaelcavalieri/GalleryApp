package com.androstock.galleryapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryPreview extends AppCompatActivity {

    ImageView GalleryPreviewImg;
    String path;
    Button shareButton;
    Uri imageUri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        imageUri=null;
        shareButton = (Button) findViewById(R.id.share_image);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(imageUri);
            }
        });

        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_VIEW) && intent.getType() !=null)
            imageUri = intent.getData();
        else if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_SEND) && intent.getType() !=null)
            imageUri= (Uri)intent.getExtras().get(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                Glide.with(GalleryPreview.this)
                        .load(new File(getPath(imageUri))) // Uri of the picture
                        .into(GalleryPreviewImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            path = intent.getStringExtra("path");
            imageUri = Uri.parse(path);
            try {
                Glide.with(GalleryPreview.this)
                        .load(new File(path)) // Uri of the picture
                        .into(GalleryPreviewImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void shareImage(Uri imageUri){
        Bitmap icon = ((BitmapDrawable)GalleryPreviewImg.getDrawable()).getBitmap();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));

    }

    private String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
