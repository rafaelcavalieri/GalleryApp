package com.racavalieri.quickgalleryorg;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.racavalieri.quickgalleryorg.Entity.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;



public class GalleryPreview extends AppCompatActivity {

    private ImageView GalleryPreviewImg;
    private String path;
    private Uri imageUri=null;
    private ImageButton shareButton;
    private ImageButton editImageData;
    private EditText edtImageDataKeyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        imageUri=null;
        shareButton = (ImageButton) findViewById(R.id.share_image);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(imageUri);
            }
        });
        editImageData = (ImageButton) findViewById(R.id.edit_image_data);
        editImageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image i = new Image();
                i.setKeywords("Teste");
                i.setPath(imageUri.getPath());
                i.setLastModified("");
                editImageData(i);
            }
        });

        edtImageDataKeyWords= (EditText) findViewById(R.id.edt_edit_image_data_key_words);


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
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+Environment.getExternalStorageDirectory().getPath()+"/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));

    }

    private void editImageData(final Image i){
        final Dialog dialog = new Dialog(GalleryPreview.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_image_data);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button cancelButton = (Button) dialog.findViewById(R.id.edt_edit_image_data_button_cancel);
        Button saveButton = (Button) dialog.findViewById(R.id.edt_edit_image_data_button_save);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(GalleryPreview.this, "dados da imagem salvos",Toast.LENGTH_LONG);
                toast.show();

                //db.insertImageData(i, getApplicationContext());


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*private void addCategoryScreen(){
        final Dialog dialog = new Dialog(GalleryPreview.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_category);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button cancelButton = (Button) dialog.findViewById(R.id.add_category_button_cancel);
        Button saveButton = (Button) dialog.findViewById(R.id.add_category_button_save);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(GalleryPreview.this, "categoria salva",Toast.LENGTH_LONG);
                toast.show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }*/

    private String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
