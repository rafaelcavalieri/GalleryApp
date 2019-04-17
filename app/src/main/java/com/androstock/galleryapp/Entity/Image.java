package com.androstock.galleryapp.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Image{
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "keywords")
    public String keywords;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "album")
    public String album;

    @ColumnInfo(name = "lastModified")
    public String lastModified;
}
