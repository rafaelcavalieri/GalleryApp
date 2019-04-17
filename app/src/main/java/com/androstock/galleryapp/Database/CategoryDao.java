package com.androstock.galleryapp.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.androstock.galleryapp.Entity.Category;
import com.androstock.galleryapp.Entity.Image;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    List<Category> getAll();

    @Query("SELECT * FROM category WHERE uid IN (:categoryIds)")
    List<Category> loadAllByIds(int[] categoryIds);

    @Query("SELECT * FROM category WHERE category IN (:category) ")
    Image findByCategory(String[] category);

    @Update(onConflict = REPLACE)
    void updateTrail(Category category);

    @Insert
    void insertAll(Category... categories);

    @Delete
    void delete(Category category);
}