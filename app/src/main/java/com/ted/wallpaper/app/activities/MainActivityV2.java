package com.ted.wallpaper.app.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVAnalytics;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.ted.wallpaper.app.R;
import com.ted.wallpaper.app.fragments.ImagesFragmentV2;
import com.ted.wallpaper.app.models.leancloud.ImageListInfo;
import com.ted.wallpaper.app.other.Constants;
import com.ted.wallpaper.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainActivityV2 extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ImagesFragmentV2 contentFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;

    /**当前的分类选择*/
    private int mNowCategory = Constants.NEW;

    public enum Category {
        CLOSE(Constants.CLOSE),
        NEW(Constants.NEW),
        ALL(Constants.ALL),
        LOVED(Constants.LOVED),
        BUILDINGS(Constants.BUILDINGS),
        FOOD(Constants.FOOD),
        NATURE(Constants.NATURE),
        PEOPLE(Constants.PEOPLE),
        TECHNOLOGY(Constants.TECHNOLOGY),
        OBJECTS(Constants.OBJECTS),
        OTHER(Constants.OTHER);


        public final int id;

        Category(int id) {
            this.id = id;
        }
    }

    private OnFilterChangedListener onFilterChangedListener;

    public void setOnFilterChangedListener(OnFilterChangedListener onFilterChangedListener) {
        this.onFilterChangedListener = onFilterChangedListener;
    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }

    @Override
    public ScreenShotable onSwitch(Resourceble resourceble, ScreenShotable screenShotable, int position) {
        if(resourceble.getCategoryId() < 0 || resourceble.getCategoryId() == mNowCategory){
            return screenShotable;
        }else {
            return changeFragment(screenShotable, position, resourceble.getCategoryId());
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);
        contentFragment = new ImagesFragmentV2();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);
    }

    private void createMenuList() {
        SlideMenuItem menuClose = new SlideMenuItem().setItemCategoryId(Category.CLOSE.id).setIicon(GoogleMaterial.Icon.gmd_arrow_back);
        list.add(menuClose);
        SlideMenuItem menuNew = new SlideMenuItem().setItemCategoryId(Category.NEW.id).setIicon(GoogleMaterial.Icon.gmd_filter_9_plus);
        list.add(menuNew);
        SlideMenuItem menuAll = new SlideMenuItem().setItemCategoryId(Category.ALL.id).setIicon(GoogleMaterial.Icon.gmd_perm_media);
        list.add(menuAll);
        SlideMenuItem menuBuild = new SlideMenuItem().setItemCategoryId(Category.BUILDINGS.id).setIicon(GoogleMaterial.Icon.gmd_location_city);
        list.add(menuBuild);
        SlideMenuItem menuFood = new SlideMenuItem().setItemCategoryId(Category.FOOD.id).setIicon(GoogleMaterial.Icon.gmd_local_bar);
        list.add(menuFood);
        SlideMenuItem menuNature = new SlideMenuItem().setItemCategoryId(Category.NATURE.id).setIicon(GoogleMaterial.Icon.gmd_local_florist);
        list.add(menuNature);
        SlideMenuItem menuObject = new SlideMenuItem().setItemCategoryId(Category.OBJECTS.id).setIicon(GoogleMaterial.Icon.gmd_style);
        list.add(menuObject);
        SlideMenuItem menuPeople = new SlideMenuItem().setItemCategoryId(Category.PEOPLE.id).setIicon(GoogleMaterial.Icon.gmd_person);
        list.add(menuPeople);
        SlideMenuItem menuTechnology = new SlideMenuItem().setItemCategoryId(Category.TECHNOLOGY.id).setIicon(GoogleMaterial.Icon.gmd_local_see);
        list.add(menuTechnology);
        SlideMenuItem menuOther = new SlideMenuItem().setItemCategoryId(Category.OTHER.id).setIicon(GoogleMaterial.Icon.gmd_loyalty);
        list.add(menuOther);
    }


    private void setActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent(mNowCategory);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_open_source).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_info_outline).color(Color.WHITE).actionBarSize());
        menu.findItem(R.id.action_shuffle).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_shuffle).paddingDp(1).color(Color.WHITE).actionBarSize());

        menu.findItem(R.id.action_shuffle).setVisible(mNowCategory == Category.ALL.id);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_source) {
            new Libs.Builder()
                    .withFields(R.string.class.getFields())
                    .withActivityTitle(getString(R.string.action_open_source))
                    .withActivityTheme(R.style.MaterialDrawerTheme_ActionBar)
                    .withLibraries("rxJava", "rxAndroid")
                    .start(this);

            return true;
        }
        return false;
    }

    private ScreenShotable changeFragment(ScreenShotable screenShotable, int topPosition,int categoryId) {
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
        if(null == contentFragment) contentFragment = new ImagesFragmentV2();
        if (onFilterChangedListener != null) {
            onFilterChangedListener.onFilterChanged(categoryId);
        }
        return contentFragment;
    }

    public void switchActionBarMenu(int category){
        mNowCategory = category;
        invalidateOptionsMenu();
    }

    public interface OnFilterChangedListener {
        void onFilterChanged(int filter);
    }

    /***
     * 更新分类信息
     * @param imageListInfo
     */
    public void updateImageCategoryInfo(ImageListInfo imageListInfo) {
        if(null == imageListInfo)return;
        Resources res = getResources();
        String toolbarTitle = res.getString(R.string.app_name);
        String toolbarSubTitle = "";
        switch (mNowCategory){
            case Constants.NEW:
                toolbarTitle = res.getString(R.string.category_new_10);
                String updateTime = Utils.getFormatDateStr(imageListInfo.getNewPhotoUpdateTime());
                toolbarSubTitle = TextUtils.isEmpty(updateTime)?res.getString(R.string.update_time_default):res.getString(R.string.update_time_auto,updateTime);
                break;
            case Constants.ALL:
                toolbarTitle = res.getString(R.string.category_all);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getAll());
                break;
            case Constants.LOVED:
                toolbarTitle = res.getString(R.string.category_loved);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getAll());
                break;
            case Constants.BUILDINGS:
                toolbarTitle = res.getString(R.string.category_buildings);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getBuilding());
                break;
            case Constants.FOOD:
                toolbarTitle = res.getString(R.string.category_food);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getFood());
                break;
            case Constants.NATURE:
                toolbarTitle = res.getString(R.string.category_nature);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getNature());
                break;
            case Constants.PEOPLE:
                toolbarTitle = res.getString(R.string.category_people);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getPeople());
                break;
            case Constants.TECHNOLOGY:
                toolbarTitle = res.getString(R.string.category_technology);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getTechnology());
                break;
            case Constants.OBJECTS:
                toolbarTitle = res.getString(R.string.category_objects);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getObject());
                break;
            case Constants.OTHER:
                toolbarTitle = res.getString(R.string.category_other);
                toolbarSubTitle = res.getString(R.string.categories_count,imageListInfo.getOther());
                break;
            default:
                break;
        }
        toolbar.setTitle(toolbarTitle);
        toolbar.setTitleTextAppearance(this, R.style.toolbar_title_style);
        toolbar.setSubtitle(toolbarSubTitle);
        toolbar.setSubtitleTextAppearance(this, R.style.toolbar_subtitle_style);
    }
}
