package com.campusone.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private static final int MAX_CACHE_SIZE = 15 * 1024 * 1024; // 15MB cache
    private static final LruCache<String, Bitmap> memoryCache = new LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void load(String url, ImageView imageView) {
        if (imageView == null) return;
        if (url == null || url.trim().isEmpty()) {
            imageView.setVisibility(View.GONE);
            return;
        }

        final String cleanUrl = url.trim();

        // Check memory cache
        Bitmap cached = memoryCache.get(cleanUrl);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            imageView.setVisibility(View.VISIBLE);
            return;
        }

        // Tag view to avoid incorrect recycling in RecyclerView
        imageView.setTag(cleanUrl);

        executor.execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(cleanUrl).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                conn.disconnect();

                if (bitmap != null) {
                    memoryCache.put(cleanUrl, bitmap);
                    mainHandler.post(() -> {
                        if (cleanUrl.equals(imageView.getTag())) {
                            imageView.setImageBitmap(bitmap);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } catch (Exception e) {
                // Ignore network errors gracefully
            }
        });
    }
}
