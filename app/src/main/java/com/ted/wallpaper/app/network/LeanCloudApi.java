package com.ted.wallpaper.app.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.ted.wallpaper.app.CustomApplication;
import com.ted.wallpaper.app.models.Image;
import com.ted.wallpaper.app.models.ImageResults;
import com.ted.wallpaper.app.other.Constants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import rx.Observable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class LeanCloudApi {

    public static final String ENDPOINT = Constants.LEAN_SERVER_IP + Constants.LEAN_SERVER_IP_VERSION;

    private final LeanCloudService mWebService;

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public LeanCloudApi() {
        Cache cache;
        OkHttpClient okHttpClient = null;

        try {
            File cacheDir = new File(CustomApplication.getContext().getCacheDir().getPath(), "pic_cache.json");
            cache = new Cache(cacheDir, 10 * 1024 * 1024);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        } catch (Exception e) {
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(mRequestInterceptor)
                .build();
        mWebService = restAdapter.create(LeanCloudService.class);
    }

    private RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
            request.addHeader("X-AVOSCloud-Application-Id", Constants.LEAN_CLOUD_ID);
            request.addHeader("X-AVOSCloud-Application-Key", Constants.LEAN_CLOUD_KEY);
            request.addHeader("Content-Type", "application/json");
        }
    };

    public interface LeanCloudService {
        @GET("/pictures")
        Observable<ImageResults> listImages();
    }

    public interface RandomUnsplashService {
        @GET("/random")
        Image random();
    }

    public Observable<ImageResults> fetchImages() {
        return mWebService.listImages();
    }


    //keep the filtered array so we can reuse it later :D
    private ArrayList<Image> featured = null;

    public ArrayList<Image> filterFeatured(ArrayList<Image> images) {
        if (featured == null) {
            ArrayList<Image> list = new ArrayList<Image>(images);
            for (Iterator<Image> it = list.iterator(); it.hasNext(); ) {
                if (it.next().getFeatured() != 1) {
                    it.remove();
                }
            }
            featured = list;
        }
        return featured;
    }

    public static int countFeatured(ArrayList<Image> images) {
        int count = 0;
        for (Image image : images) {
            if (image.getFeatured() == 1) {
                count = count + 1;
            }
        }
        return count;
    }

    public ArrayList<Image> filterCategory(ArrayList<Image> images, int filter) {
        ArrayList<Image> list = new ArrayList<Image>(images);
        for (Iterator<Image> it = list.iterator(); it.hasNext(); ) {
            if ((it.next().getCategory() & filter) != filter) {
                it.remove();
            }
        }
        return list;
    }

    public static int countCategory(ArrayList<Image> images, int filter) {
        int count = 0;
        for (Image image : images) {
            if ((image.getCategory() & filter) == filter) {
                count = count + 1;
            }
        }
        return count;
    }
}
