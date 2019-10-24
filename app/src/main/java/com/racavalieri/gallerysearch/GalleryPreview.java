package com.racavalieri.gallerysearch;

import android.app.Dialog;
import android.content.ContentValues;
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
import com.racavalieri.gallerysearch.Database.DAO;
import com.racavalieri.gallerysearch.Entity.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class GalleryPreview extends AppCompatActivity {

    private static DAO dao;
    private ImageView GalleryPreviewImg;
    private String path;
    private Uri imageUri = null;
    private ImageButton shareButton;
    private ImageButton editImageData;
    private EditText edtImageDataKeyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        dao = new DAO(getApplicationContext());

        imageUri = null;
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

                if (dao.exist(path, "IMAGE", "PATH", "PATH")) {
                    Cursor selectedImage = dao.select("UID, KEYWORDS, PATH, LASTMODIFIED, LATITUDE, LONGITUDE"
                            , "IMAGE", "PATH LIKE '%" + imageUri.getPath() + "%'");
                    if (selectedImage != null && selectedImage.moveToNext()) {
                        i.setKeywords(selectedImage.getString(1));
                    }
                }
                i.setPath(imageUri.getPath());
                editImageKeywords(i);
            }
        });

        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW) && intent.getType() != null)
            imageUri = intent.getData();
        else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND) && intent.getType() != null)
            imageUri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                Glide.with(GalleryPreview.this)
                        .load(new File(getPath(imageUri))) // Uri of the picture
                        .into(GalleryPreviewImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
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

    private void shareImage(Uri imageUri) {
        Bitmap icon = ((BitmapDrawable) GalleryPreviewImg.getDrawable()).getBitmap();
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
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, getString(R.string.share_image)));

    }

    private void editImageKeywords(final Image i) {
        final Dialog dialog = new Dialog(GalleryPreview.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_image_data);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        edtImageDataKeyWords = dialog.findViewById(R.id.edt_edit_image_data_key_words);
        edtImageDataKeyWords.setText(i.getKeywords());

        Button cancelButton = dialog.findViewById(R.id.edt_edit_image_data_button_cancel);
        Button saveButton = dialog.findViewById(R.id.edt_edit_image_data_button_save);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date now = Calendar.getInstance().getTime();
                    String nowAsString = df.format(now);
                    String[] argsToUpdate = new String[1];
                    argsToUpdate[0] = i.getPath();

                    ContentValues values = new ContentValues();
                    values.put("KEYWORDS", edtImageDataKeyWords.getText().toString());
                    values.put("PATH", i.getPath());
                    values.put("LASTMODIFIED", nowAsString);

                    if (dao.exist(i.getPath(), "IMAGE", "PATH", "PATH"))
                        dao.update("IMAGE", values, "PATH", argsToUpdate);

                    else
                        dao.insert("IMAGE", values);
                    Toast.makeText(GalleryPreview.this, getString(R.string.data_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
