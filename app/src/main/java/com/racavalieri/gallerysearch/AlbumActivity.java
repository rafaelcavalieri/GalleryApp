package com.racavalieri.gallerysearch;

import android.app.Activity;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AlbumActivity extends AppCompatActivity {
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;

    HashMap<String, HashMap< String, String >> selectedItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360) {
            dp = (dp - 17) / 2;
            float px = com.racavalieri.gallerysearch.Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
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

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };

            Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
            Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                imageList.add(com.racavalieri.gallerysearch.Function.mappingInbox(album, path, timestamp, com.racavalieri.gallerysearch.Function.converToTime(timestamp), null));
            }
            cursor.close();
            Collections.sort(imageList, new com.racavalieri.gallerysearch.MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
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

    private void onItemClicked(int position, boolean isLongClick) {
        if(selectedItems.size() == 0 && !isLongClick) {
            Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
            intent.putExtra("path", imageList.get(+position).get(Function.KEY_PATH));
            startActivity(intent);
            return;
        }

        final HashMap<String, String> selectedItem = imageList.get(position);
        String path = selectedItem.get(Function.KEY_PATH);

        if(selectedItems.containsKey(path)) {
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

        if(selectedItems.size() == 0) {

        }
    }

    private void addSelection(HashMap<String, String> itemAtPosition) {
        itemAtPosition.put(Function.KEY_SELECTED, "");
        selectedItems.put(itemAtPosition.get(Function.KEY_PATH), itemAtPosition);
        final SingleAlbumAdapter adapter = (SingleAlbumAdapter) galleryGridView.getAdapter();
        adapter.notifyDataSetChanged();


    }

    private ArrayList<String> getSelectedPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (HashMap<String, String> value : selectedItems.values()) {
            final String path = value.get(Function.KEY_PATH);
            paths.add(path);
        }

        return paths;
    }
}

class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public SingleAlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
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

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);

        if(song.get(Function.KEY_SELECTED) != null) {
            holder.selectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIcon.setVisibility(View.GONE);
        }

        try {

            Glide.with(activity)
                    .load(new File(song.get(Function.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);
        } catch (Exception e) {}
        return convertView;
    }
}

class SingleAlbumViewHolder {
    ImageView galleryImage;
    ImageView selectedIcon;
}
