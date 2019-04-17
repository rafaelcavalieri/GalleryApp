package com.androstock.galleryapp.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Category {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "category")
    public String category;
}
