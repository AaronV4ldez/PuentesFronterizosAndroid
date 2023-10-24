package mx.gob.puentesfronterizos.lineaexpres;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class checkInternet {

    public boolean waitForInternetAvailability(Context context) {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = new boolean[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = isInternetAvailable(context);
                latch.countDown();
            }
        }).start();

        try {
            latch.await(); // Espera hasta que el contador llegue a 0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result[0];
    }

    private boolean isInternetAvailable(Context context) {
        boolean isNetworkAvailable = isNetworkAvailable(context);
        boolean hasInternetAccess = hasInternetAccess();
        return isNetworkAvailable && hasInternetAccess;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean hasInternetAccess() {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
            connection.setRequestProperty("User-Agent", "ConnectionTest");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(1500);
            connection.connect();
            int responseCode = connection.getResponseCode();
            return (responseCode == 200);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
