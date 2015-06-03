package co.creativev.networksupport;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class NetworkBuilder {
    public static final long HTTP_TIMEOUT = 10 * 1000;
    public static final long CACHE_SIZE = 1024 * 1024 * 10l;  // 10 MB HTTP Cache

    public static RestAdapter.Builder defaultRestAdapterBuilder(Context context, String server) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd")
                .create();
        return new RestAdapter.Builder()
                .setEndpoint(server)
                .setLogLevel(RestAdapter.LogLevel.valueOf(context.getString(R.string.retrofit_log_level)))
                .setClient(buildClient(context))
                .setConverter(new GsonConverter(gson));
    }

    private static Client buildClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL));
        File cacheDir = new File(context.getCacheDir(), "http-cache");
        Cache cache = new Cache(cacheDir, CACHE_SIZE);
        okHttpClient.setCache(cache);
        return new OkClient(okHttpClient);
    }
}
