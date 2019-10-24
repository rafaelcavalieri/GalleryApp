package com.racavalieri.gallerysearch;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.racavalieri.gallerysearch.Database.DAO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class AlbumActivity extends AppCompatActivity {
    private static DAO dao;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;
    RelativeLayout topButtons;
    HashMap<String, HashMap<String, String>> selectedItems = new HashMap<>();
    private EditText edtImageDataKeyWords;

    private void editMultipleImagesKeywords() {
        final Dialog dialog = new Dialog(AlbumActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_multiple_image_data);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        edtImageDataKeyWords = dialog.findViewById(R.id.edt_edit_image_data_key_words);

        Button cancelButton = dialog.findViewById(R.id.edt_edit_image_data_button_cancel);
        Button replaceButton = dialog.findViewById(R.id.edt_edit_image_data_button_replace);
        Button addButton = dialog.findViewById(R.id.edt_edit_image_data_button_add);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imagesPaths = getSelectedPaths();

                for (String path : imagesPaths) {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date now = Calendar.getInstance().getTime();
                        String nowAsString = df.format(now);
                        String[] argsToUpdate = {"" + path};

                        ContentValues values = new ContentValues();
                        values.put("KEYWORDS", edtImageDataKeyWords.getText().toString());
                        values.put("PATH", path);
                        values.put("LASTMODIFIED", nowAsString);

                        if (dao.exist(path, "IMAGE", "PATH", "PATH"))
                            dao.update("IMAGE", values, "PATH = ?", argsToUpdate);

                        else
                            dao.insert("IMAGE", values);
                        Toast.makeText(AlbumActivity.this, getString(R.string.data_saved), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imagesPaths = getSelectedPaths();

                for (String path : imagesPaths) {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date now = Calendar.getInstance().getTime();
                        String nowAsString = df.format(now);
                        String[] argsToUpdate = {"" + path};


                        ContentValues values = new ContentValues();
                        values.put("PATH", path);
                        values.put("LASTMODIFIED", nowAsString);

                        if (dao.exist(path, "IMAGE", "PATH", "PATH")) {
                            Cursor selectedImage = dao.select("UID, KEYWORDS, PATH, LASTMODIFIED, LATITUDE, LONGITUDE"
                                    , "IMAGE", "PATH LIKE '%" + path + "%'");
                            if (selectedImage != null && selectedImage.moveToNext()) {
                                values.put("KEYWORDS", selectedImage.getString(1) + ", " + edtImageDataKeyWords.getText().toString());
                            } else {
                                values.put("KEYWORDS", edtImageDataKeyWords.getText().toString());
                            }
                            dao.update("IMAGE", values, "PATH = ?", argsToUpdate);
                        } else {
                            dao.insert("IMAGE", values);
                        }
                        Toast.makeText(AlbumActivity.this, getString(R.string.data_saved), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();
    }

    private void shareMultipleImages() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.setType("image/jpeg");

        ArrayList<Uri> files = new ArrayList<Uri>();

        for (String path : getSelectedPaths()) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);
        dao = new DAO(getApplicationContext());

        topButtons = findViewById(R.id.topButtons);

        ImageButton shareMultiple = findViewById(R.id.share_multiple_images);
        shareMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMultipleImages();
            }
        });

        ImageButton editMultiple = findViewById(R.id.edit_multiple_image_data);
        editMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMultipleImagesKeywords();
            }
        });

        topButtons.setVisibility(View.GONE);

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = com.racavalieri.gallerysearch.Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }

    private void onItemClicked(int position, boolean isLongClick) {
        if (selectedItems.size() == 0 && !isLongClick) {
            Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
            intent.putExtra("path", imageList.get(+position).get(Function.KEY_PATH));
            startActivity(intent);
            return;
        }

        final HashMap<String, String> selectedItem = imageList.get(position);
        String path = selectedItem.get(Function.KEY_PATH);

        if (selectedItems.containsKey(path)) {
            removeSelection(selectedItem);
        } else {
            addSelection(selectedItem);
        }
    }

    private void removeSelection(HashMap<String, String> itemAtPosition) {
        itemAtPosition.remove(Function.KEY_SELECTED);
        selectedItems.remove(itemAtPosition.get(Function.KEY_PATH));
        final SingleAlbumAdapter adapter = (SingleAlbumAdapter) galleryGridView.getAdapter();
        adapter.notifyDataSetChanged();

        if (selectedItems.size() == 0) {
            topButtons.setVisibility(View.GONE);
        }
    }

    private void addSelection(HashMap<String, String> itemAtPosition) {
        itemAtPosition.put(Function.KEY_SELECTED, "");
        selectedItems.put(itemAtPosition.get(Function.KEY_PATH), itemAtPosition);
        final SingleAlbumAdapter adapter = (SingleAlbumAdapter) galleryGridView.getAdapter();
        adapter.notifyDataSetChanged();

        topButtons.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> getSelectedPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (HashMap<String, String> value : selectedItems.values()) {
            final String path = value.get(Function.KEY_PATH);
            paths.add(path);
        }

        return paths;
    }

    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                imageList.add(com.racavalieri.gallerysearch.Function.mappingInbox(album, path, timestamp, com.racavalieri.gallerysearch.Function.converToTime(timestamp), null));
            }
            cursor.close();
            Collections.sort(imageList, new com.racavalieri.gallerysearch.MapComparator(Function.KEY_TIMESTAMP, "dsc"));
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            final SingleAlbumAdapter adapter = new SingleAlbumAdapter(AlbumActivity.this, imageList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    onItemClicked(position, false);
                }
            });
            galleryGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemClicked(position, true);
                    return true;
                }
            });
        }
    }
}

class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public SingleAlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.single_album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.selectedIcon = convertView.findViewById(R.id.checkIcon);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        if (song.get(Function.KEY_SELECTED) != null) {
            holder.selectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIcon.setVisibility(View.GONE);
        }

        try {

            Glide.with(activity)
                    .load(new File(song.get(Function.KEY_PATH)))
                    .into(holder.galleryImage);
        } catch (Exception e) {
        }
        return convertView;
    }
}

class SingleAlbumViewHolder {
    ImageView galleryImage;
    ImageView selectedIcon;
}
