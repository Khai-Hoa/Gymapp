package com.example.gym_manager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCart(Cart cart);

    @Query("SELECT * FROM cart_tb")
    List<Cart> getAllCart();

    @Query("DELETE FROM cart_tb WHERE id=:id")
    void deleteCartItemById(int id);
    @Query("DELETE FROM cart_tb")
    void deleteAllCarts();

    @Query("SELECT * FROM cart_tb WHERE purchaseTime BETWEEN :startDate AND :endDate")
    List<Cart> getCartsBetweenDates(Date startDate, Date endDate);


}
