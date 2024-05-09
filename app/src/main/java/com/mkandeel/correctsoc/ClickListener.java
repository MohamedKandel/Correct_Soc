package com.mkandeel.correctsoc;

import android.os.Bundle;

import androidx.annotation.Nullable;

public interface ClickListener {
    void onItemClickListener(int position, @Nullable Bundle extra);

    void onLongItemClickListener(int position, @Nullable Bundle extra);
}