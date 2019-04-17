package com.androstock.galleryapp.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.androstock.galleryapp.Entity.Image;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM image")
    List<Image> getAll();

    @Query("SELECT * FROM image WHERE uid IN (:imageIds)")
    List<Image> loadAllByIds(int[] imageIds);

    @Query("SELECT * FROM image WHERE keywords LIKE :keyword OR " +
            "category LIKE :keyword LIMIT 1")
    Image findByKeyword(String keyword);

    @Query("SELECT * FROM image WHERE keywords IN (:keywords) OR " +
            "category IN (:keywords) LIMIT 1")
    Image findByVariousKeyword(String[] keywords);

    @Update(onConflict = REPLACE)
    void updateImage(Image image);

    @Insert
    void insertAll(Image... images);

    @Delete
    void delete(Image image);
}