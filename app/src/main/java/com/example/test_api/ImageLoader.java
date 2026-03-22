package com.example.test_api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private Handler handler = new Handler(Looper.getMainLooper());
    private LruCache<String, Bitmap> cache = new LruCache<>(20); // кэш на 20 изображений

    public void loadImage(String urlString, ImageView imageView) {
        // Проверяем кэш
        Bitmap cached = cache.get(urlString);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                // Сохраняем в кэш
                cache.put(urlString, bitmap);

                handler.post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> imageView.setImageResource(android.R.drawable.ic_menu_gallery));
            }
        });
    }

    // Простой LruCache
    private static class LruCache<K, V> {
        private final java.util.LinkedHashMap<K, V> map;
        private final int maxSize;
        private int size;

        public LruCache(int maxSize) {
            this.maxSize = maxSize;
            this.map = new java.util.LinkedHashMap<>(0, 0.75f, true);
        }

        public V get(K key) {
            return map.get(key);
        }

        public void put(K key, V value) {
            if (value instanceof Bitmap) {
                size += ((Bitmap) value).getByteCount() / 1024;
            }
            map.put(key, value);
            trimToSize();
        }

        private void trimToSize() {
            while (size > maxSize * 1024) {
                java.util.Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
                map.remove(toEvict.getKey());
                if (toEvict.getValue() instanceof Bitmap) {
                    size -= ((Bitmap) toEvict.getValue()).getByteCount() / 1024;
                }
            }
        }
    }
}
