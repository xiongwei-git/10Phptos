package com.ted.wallpaper.app.muzei;

import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.ted.wallpaper.app.models.Image;
import com.ted.wallpaper.app.network.UnsplashApi;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

public class WallSplashSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "wall:splash";

    private static final int ROTATE_TIME_MILLIS = 3 * 60 * 60 * 1000; // rotate every 3 hours


    private int mWallpaperWidth = -1;
    private int mWallpaperHeight = -1;

    public WallSplashSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);

        mWallpaperWidth = WallpaperManager.getInstance(this).getDesiredMinimumWidth();
        mWallpaperHeight = WallpaperManager.getInstance(this).getDesiredMinimumHeight();
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(UnsplashApi.ENDPOINT)
                .setConverter(new GsonConverter(UnsplashApi.gson))
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        if (retrofitError != null && retrofitError.getResponse() != null && retrofitError.getKind() != null) {
                            int statusCode = retrofitError.getResponse().getStatus();
                            if (retrofitError.getKind() == RetrofitError.Kind.NETWORK || (500 <= statusCode && statusCode < 600)) {
                                return new RetryException();
                            }
                        }
                        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                        return retrofitError;
                    }
                })
                .build();

        UnsplashApi.RandomUnsplashService service = restAdapter.create(UnsplashApi.RandomUnsplashService.class);
        try {
            Image image = service.random();

            if (image == null) {
                scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                return;
            }

            publishArtwork(new Artwork.Builder()
                    .imageUri(Uri.parse(image.getHighResImage(mWallpaperWidth, mWallpaperHeight)))
                    .title(image.getAuthor())
                    .byline(image.getReadableModified_Date())
                    .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(image.getUrl())))
                    .build());
        } catch (Exception ex) {
            Log.e("wallsplash", "WallSplashSource: " + ex.toString());
        }

        //schedule the next update ;)
        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }
}
