package com.example.gym_manager.listener;

import com.example.gym_manager.database.Cart;

public interface OnCartItemDeletedListener {
    void onCartItemDeleted(Cart cart);
}
