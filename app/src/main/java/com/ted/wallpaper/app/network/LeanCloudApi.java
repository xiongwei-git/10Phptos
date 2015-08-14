package com.ted.wallpaper.app.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.ted.wallpaper.app.CustomApplication;
import com.ted.wallpaper.app.models.Image;
import com.ted.wallpaper.app.models.leancloud.ImageListInfoResults;
import com.ted.wallpaper.app.models.leancloud.ImageResults;
import com.ted.wallpaper.app.other.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public class LeanCloudApi {
    /**每次加载条目*/
    public static final int LOAD_LIMIT = 1000;

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
        @GET("/PictureInfo")
        Observable<ImageListInfoResults> getImagesInfo();

        @GET("/NewPictures")
        Observable<ImageResults> getNewImages();

        @GET("/AllPictures")
        Observable<ImageResults> listImages();

        /**按照更新时间由新到旧排序获取数据*/
        @GET("/AllPictures?order=updatedAt")
        Observable<ImageResults> getMoreImages(
                @Query("skip") int skip,
                @Query("limit") int limit
        );
    }

    public Observable<ImageListInfoResults> getImagesInfo() {
        return mWebService.getImagesInfo();
    }

    public Observable<ImageResults> getNewImages() {
        return mWebService.getNewImages();
    }

    public Observable<ImageResults> fetchImages() {
        return mWebService.listImages();
    }

    public Observable<ImageResults> getMoreImages(int skip,int limit) {
        return mWebService.getMoreImages(skip,limit);
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
        ArrayList<Image> list = new ArrayList<>(images);
        for (Iterator<Image> it = list.iterator(); it.hasNext(); ) {
            if ((it.next().getCategory() & filter) != filter) {
                it.remove();
            }
        }
        return list;
    }

    public ArrayList<Image> filterOtherCategory(ArrayList<Image> images) {
        ArrayList<Image> list = new ArrayList<>(images);
        for (Iterator<Image> it = list.iterator(); it.hasNext(); ) {
            int category = it.next().getCategory();
            if (category == 1 || category == 2 || category == 4 || category == 8 || category == 16 || category == 32) {
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
