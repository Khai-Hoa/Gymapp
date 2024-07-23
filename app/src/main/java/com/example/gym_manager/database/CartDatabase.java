package com.example.gym_manager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Cart.class},version = 2,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDao cartDao();

    private static volatile  CartDatabase INSTANCE;
    public static CartDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CartDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CartDatabase.class, "cart_database")
                            .addMigrations(Migrations.MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
