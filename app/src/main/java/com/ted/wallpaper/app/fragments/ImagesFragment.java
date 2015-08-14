package com.ted.wallpaper.app.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVAnalytics;
import com.ted.wallpaper.app.R;
import com.ted.wallpaper.app.activities.DetailActivity;
import com.ted.wallpaper.app.activities.MainActivity;
import com.ted.wallpaper.app.adapters.ImageAdapter;
import com.ted.wallpaper.app.models.Image;
import com.ted.wallpaper.app.models.leancloud.ImageListInfo;
import com.ted.wallpaper.app.models.leancloud.ImageListInfoResults;
import com.ted.wallpaper.app.models.leancloud.ImageResults;
import com.ted.wallpaper.app.network.LeanCloudApi;
import com.ted.wallpaper.app.other.OnItemClickListener;
import com.ted.wallpaper.app.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.RetrofitError;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tr.xip.errorview.ErrorView;
import tr.xip.errorview.RetryListener;

public class ImagesFragment extends Fragment {

    public static SparseArray<Bitmap> photoCache = new SparseArray<>(1);

    private LeanCloudApi mApi = new LeanCloudApi();

    private ImageAdapter mImageAdapter;
    private ArrayList<Image> mAllImages;
    private ArrayList<Image> mNewImages;
    private ArrayList<Image> mCurrentImages;
    private RecyclerView mImageRecycler;
    private ProgressBar mImagesProgress;
    private ErrorView mImagesErrorView;
    private ImageListInfo mImageListInfo;

    /**点击某个分类项目时，如果没有数据，在加载完数据时，要执行分类动作*/
    private int mNeedToCategoryFilter = -1;

    private MainActivity.OnFilterChangedListener mMainOnFilterChangedListener = new MainActivity.OnFilterChangedListener() {
        @Override
        public void onFilterChanged(int filter) {
            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).switchActionBarMenu(filter);
            }
            if (filter == MainActivity.Category.NEW.id) {
                getNewPhotos();
            } else {
                showCategory(filter);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (ImagesFragment.this.getActivity() instanceof MainActivity) {
            ((MainActivity) ImagesFragment.this.getActivity()).setOnFilterChangedListener(mMainOnFilterChangedListener);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        mImageRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_last_images_recycler);
        mImagesProgress = (ProgressBar) rootView.findViewById(R.id.fragment_images_progress);
        mImagesErrorView = (ErrorView) rootView.findViewById(R.id.fragment_images_error_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mImageRecycler.setLayoutManager(gridLayoutManager);
        mImageRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mImageAdapter = new ImageAdapter();
        mImageAdapter.setOnItemClickListener(recyclerRowClickListener);
        mImageRecycler.setAdapter(mImageAdapter);

        getImageListInfo();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(ImagesFragment.class.getCanonicalName());
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(ImagesFragment.class.getCanonicalName());
    }

    private void getImageListInfo() {
        if (mImageListInfo != null) {
            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).updateImageCategoryInfo(mImageListInfo);
            }
        } else {
            mImagesProgress.setVisibility(View.VISIBLE);
            mImageRecycler.setVisibility(View.GONE);
            mImagesErrorView.setVisibility(View.GONE);

            mApi.getImagesInfo().cache().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getImageInfoObserver);
        }
    }


    private void getAllPhotos() {
        if(null == mAllImages){
            getMorePhotos(0, Math.min(mImageListInfo.getAll(), LeanCloudApi.LOAD_LIMIT));
        }else {
            int hasLoad = mAllImages.size();
            int all = mImageListInfo.getAll();
            /**全部加载完成*/
            if (hasLoad >= all) {
                mImagesProgress.setVisibility(View.GONE);
                mImageRecycler.setVisibility(View.VISIBLE);
                mImagesErrorView.setVisibility(View.GONE);
                /**加载完成数据之后，先判断是否需要跳转分类*/
                if(mNeedToCategoryFilter > 0){
                    int category = mNeedToCategoryFilter;
                    mNeedToCategoryFilter = -1;
                    showCategory(category);
                }else {
                    updateAdapter(mAllImages);
                }
            } else {
                getMorePhotos(hasLoad, Math.min(all - hasLoad,LeanCloudApi.LOAD_LIMIT));
            }
        }
    }

    /***
     * 与getAllPhotos配合使用
     * @param skip 起点
     * @param limit 每次加载的数目
     */
    private void getMorePhotos(int skip,int limit){
        mImagesProgress.setVisibility(View.VISIBLE);
        mImageRecycler.setVisibility(View.GONE);
        mImagesErrorView.setVisibility(View.GONE);

        mApi.getMoreImages(skip,limit).cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMoreImageListObserver);
    }

    private void getNewPhotos() {
        if (mNewImages != null && mNewImages.size() > 0) {
            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.VISIBLE);
            mImagesErrorView.setVisibility(View.GONE);
            updateAdapter(mNewImages);
        } else {
            mImagesProgress.setVisibility(View.VISIBLE);
            mImageRecycler.setVisibility(View.GONE);
            mImagesErrorView.setVisibility(View.GONE);

            mApi.getNewImages().cache().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getNewImagesObserver);
        }
    }

//    private void showFeatured() {
//        updateAdapter(mApi.filterFeatured(mAllImages));
//    }

    private void showCategory(int category) {
        if(null == mAllImages || mAllImages.size()==0){
            mNeedToCategoryFilter = category;
            getAllPhotos();
            return;
        }
        if (category == MainActivity.Category.ALL.id){
            updateAdapter(mAllImages);
            return;
        }
        if(category == MainActivity.Category.OTHER.id){
            updateAdapter(mApi.filterOtherCategory(mAllImages));
        }else {
            updateAdapter(mApi.filterCategory(mAllImages, category));
        }

    }


    /**获取照片基本信息的回调接口*/
    private Observer<ImageListInfoResults> getImageInfoObserver = new Observer<ImageListInfoResults>() {
        @Override
        public void onNext(final ImageListInfoResults imageListInfoResults) {
            if(null != imageListInfoResults && null != imageListInfoResults.getResults()){
                mImageListInfo = imageListInfoResults.getResults().get(0);
            }
            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).updateImageCategoryInfo(mImageListInfo);
            }
        }

        @Override
        public void onCompleted() {
            getNewPhotos();
        }

        @Override
        public void onError(final Throwable error) {
            if (error instanceof RetrofitError) {
                RetrofitError e = (RetrofitError) error;
                if (e.getKind() == RetrofitError.Kind.NETWORK) {
                    mImagesErrorView.setErrorTitle(R.string.error_network);
                    mImagesErrorView.setErrorSubtitle(R.string.error_network_subtitle);
                } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                    mImagesErrorView.setErrorTitle(R.string.error_server);
                    mImagesErrorView.setErrorSubtitle(R.string.error_server_subtitle);
                } else {
                    mImagesErrorView.setErrorTitle(R.string.error_uncommon);
                    mImagesErrorView.setErrorSubtitle(R.string.error_uncommon_subtitle);
                }
            }

            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.GONE);
            mImagesErrorView.setVisibility(View.VISIBLE);

            mImagesErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    getImageListInfo();
                }
            });
        }
    };

    private Observer<ImageResults> getNewImagesObserver = new Observer<ImageResults>() {
        @Override
        public void onNext(final ImageResults images) {
            mNewImages = images.getResults();
            updateAdapter(mNewImages);
            if(null != mNewImages && mNewImages.size() >0){
                if(null != mImageListInfo)
                mImageListInfo.setNewPhotoUpdateTime(Utils.FormatDateFromStr(mNewImages.get(0).getUpdatedAt()));
            }
            if (ImagesFragment.this.getActivity() instanceof MainActivity) {
                ((MainActivity) ImagesFragment.this.getActivity()).updateImageCategoryInfo(mImageListInfo);
            }
        }

        @Override
        public void onCompleted() {
            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.VISIBLE);
            mImagesErrorView.setVisibility(View.GONE);
        }

        @Override
        public void onError(final Throwable error) {
            if (error instanceof RetrofitError) {
                RetrofitError e = (RetrofitError) error;
                if (e.getKind() == RetrofitError.Kind.NETWORK) {
                    mImagesErrorView.setErrorTitle(R.string.error_network);
                    mImagesErrorView.setErrorSubtitle(R.string.error_network_subtitle);
                } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                    mImagesErrorView.setErrorTitle(R.string.error_server);
                    mImagesErrorView.setErrorSubtitle(R.string.error_server_subtitle);
                } else {
                    mImagesErrorView.setErrorTitle(R.string.error_uncommon);
                    mImagesErrorView.setErrorSubtitle(R.string.error_uncommon_subtitle);
                }
            }

            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.GONE);
            mImagesErrorView.setVisibility(View.VISIBLE);

            mImagesErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    getNewPhotos();
                }
            });
        }
    };

    private Observer<ImageResults> getMoreImageListObserver = new Observer<ImageResults>() {
        @Override
        public void onNext(final ImageResults images) {
            if(null == mAllImages)mAllImages = new ArrayList<>();
            if(null != images.getResults())mAllImages.addAll(images.getResults());
        }

        @Override
        public void onCompleted() {
            getAllPhotos();
        }

        @Override
        public void onError(final Throwable error) {
            if (error instanceof RetrofitError) {
                RetrofitError e = (RetrofitError) error;
                if (e.getKind() == RetrofitError.Kind.NETWORK) {
                    mImagesErrorView.setErrorTitle(R.string.error_network);
                    mImagesErrorView.setErrorSubtitle(R.string.error_network_subtitle);
                } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                    mImagesErrorView.setErrorTitle(R.string.error_server);
                    mImagesErrorView.setErrorSubtitle(R.string.error_server_subtitle);
                } else {
                    mImagesErrorView.setErrorTitle(R.string.error_uncommon);
                    mImagesErrorView.setErrorSubtitle(R.string.error_uncommon_subtitle);
                }
            }

            mImagesProgress.setVisibility(View.GONE);
            mImageRecycler.setVisibility(View.GONE);
            mImagesErrorView.setVisibility(View.VISIBLE);

            mImagesErrorView.setOnRetryListener(new RetryListener() {
                @Override
                public void onRetry() {
                    getAllPhotos();
                }
            });
        }
    };

    private OnItemClickListener recyclerRowClickListener = new OnItemClickListener() {

        @Override
        public void onClick(View v, int position) {

            Image selectedImage = mCurrentImages.get(position);

            Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
            detailIntent.putExtra("position", position);
            detailIntent.putExtra("selected_image", selectedImage);

            if (selectedImage.getSwatch() != null) {
                detailIntent.putExtra("swatch_title_text_color", selectedImage.getSwatch().getTitleTextColor());
                detailIntent.putExtra("swatch_rgb", selectedImage.getSwatch().getRgb());
            }

            ImageView coverImage = (ImageView) v.findViewById(R.id.item_image_img);
            if (coverImage == null) {
                coverImage = (ImageView) ((View) v.getParent()).findViewById(R.id.item_image_img);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                if (coverImage.getParent() != null) {
                    ((ViewGroup) coverImage.getParent()).setTransitionGroup(false);
                }
            }

            if (coverImage != null && coverImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) coverImage.getDrawable()).getBitmap(); //ew
                if (bitmap != null && !bitmap.isRecycled()) {
                    photoCache.put(position, bitmap);

                    // Setup the transition to the detail activity
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), coverImage, "cover");

                    startActivity(detailIntent, options.toBundle());
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shuffle) {
            if (mAllImages != null) {
                ArrayList<Image> shuffled = new ArrayList<>(mAllImages);
                Collections.shuffle(shuffled);
                mImageAdapter.updateData(shuffled);
                updateAdapter(shuffled);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * a small helper class to update the adapter
     *
     * @param images
     */
    private void updateAdapter(ArrayList<Image> images) {
        mCurrentImages = images;
        mImageAdapter.updateData(mCurrentImages);
        mImageRecycler.scrollToPosition(0);
    }
}
