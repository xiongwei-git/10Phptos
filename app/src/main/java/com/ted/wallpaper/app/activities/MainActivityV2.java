package com.ted.wallpaper.app.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import com.avos.avoscloud.AVAnalytics;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.ted.wallpaper.app.R;
import com.ted.wallpaper.app.fragments.ContentFragment;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import java.util.ArrayList;
import java.util.List;


public class MainActivityV2 extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ContentFragment contentFragment;
    private ViewAnimator viewAnimator;
    private int res = R.drawable.content_music;
    private LinearLayout linearLayout;


    /**当前的分类选择*/
    private int mNowCategory = -1;

    public enum Category {
        NEW(1000),
        ALL(1001),
        LOVED(1002),
        BUILDINGS(1),
        FOOD(2),
        NATURE(4),
        PEOPLE(8),
        TECHNOLOGY(16),
        OBJECTS(32),
        OTHER(64);


        public final int id;

        Category(int id) {
            this.id = id;
        }
    }


    public Drawer.Result result;

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
        switch (resourceble.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                return replaceFragment(screenShotable, position);
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
        contentFragment = ContentFragment.newInstance(R.drawable.content_music);
        getSupportFragmentManager().beginTransaction()
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

//        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);
//
//        result = new Drawer()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withHeader(R.layout.header)
//                .addDrawerItems(
//                        new PrimaryDrawerItem().withName(R.string.category_new_10).withIdentifier(Category.NEW.id).withIcon(GoogleMaterial.Icon.gmd_filter_9_plus),
//                        new PrimaryDrawerItem().withName(R.string.category_all).withIdentifier(Category.ALL.id).withIcon(GoogleMaterial.Icon.gmd_perm_media),
//                        //new PrimaryDrawerItem().withName(R.string.category_loved).withIdentifier(Category.LOVED.id).withIcon(GoogleMaterial.Icon.gmd_favorite),
//                        new SectionDrawerItem().withName(R.string.category_section_categories),
//                        new PrimaryDrawerItem().withName(R.string.category_buildings).withIdentifier(Category.BUILDINGS.id).withIcon(GoogleMaterial.Icon.gmd_location_city),
//                        new PrimaryDrawerItem().withName(R.string.category_food).withIdentifier(Category.FOOD.id).withIcon(GoogleMaterial.Icon.gmd_local_bar),
//                        new PrimaryDrawerItem().withName(R.string.category_nature).withIdentifier(Category.NATURE.id).withIcon(GoogleMaterial.Icon.gmd_local_florist),
//                        new PrimaryDrawerItem().withName(R.string.category_objects).withIdentifier(Category.OBJECTS.id).withIcon(GoogleMaterial.Icon.gmd_style),
//                        new PrimaryDrawerItem().withName(R.string.category_people).withIdentifier(Category.PEOPLE.id).withIcon(GoogleMaterial.Icon.gmd_person),
//                        new PrimaryDrawerItem().withName(R.string.category_technology).withIdentifier(Category.TECHNOLOGY.id).withIcon(GoogleMaterial.Icon.gmd_local_see),
//                        new PrimaryDrawerItem().withName(R.string.category_other).withIdentifier(Category.OTHER.id).withIcon(GoogleMaterial.Icon.gmd_loyalty)
//        )
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
//                        if (drawerItem != null) {
//                            if (drawerItem instanceof Nameable) {
//                                toolbar.setTitle(((Nameable) drawerItem).getNameRes());
//                            }
//                            if (onFilterChangedListener != null) {
//                                onFilterChangedListener.onFilterChanged(drawerItem.getIdentifier());
//                            }
//                        }
//                    }
//                })
//                .build();
//
//        //disable scrollbar :D it's ugly
//        result.getListView().setVerticalScrollBarEnabled(false);
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem().setName(ContentFragment.CLOSE).setIicon(GoogleMaterial.Icon.gmd_filter_9_plus);
        list.add(menuItem0);
        SlideMenuItem menuItem1 = new SlideMenuItem().setName(ContentFragment.BUILDING).setIicon(GoogleMaterial.Icon.gmd_filter_9_plus);
        list.add(menuItem1);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.BOOK, R.drawable.icn_2);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.PAINT, R.drawable.icn_3);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.CASE, R.drawable.icn_4);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.SHOP, R.drawable.icn_5);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.PARTY, R.drawable.icn_6);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.MOVIE, R.drawable.icn_7);
        list.add(menuItem7);
    }


    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    viewAnimator.showMenuContent();
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

//    /***
//     * 更新分类信息
//     * @param imageListInfo
//     */
//    public void updateImageCategoryInfo(ImageListInfo imageListInfo) {
//        if (result.getDrawerItems() != null && result.getDrawerItems().size() == 10 && imageListInfo != null) {
//            Resources res = getResources();
//            String updateTime = Utils.getFormatDateStr(imageListInfo.getNewPhotoUpdateTime());
//            updateTime = TextUtils.isEmpty(updateTime)?res.getString(R.string.update_time_default):res.getString(R.string.update_time_auto,updateTime);
//            result.updateBadge(updateTime, 0);
//            result.updateBadge(String.valueOf(imageListInfo.getAll()), 1);
//
//            result.updateBadge(String.valueOf(imageListInfo.getBuilding()), 3);
//            result.updateBadge(String.valueOf(imageListInfo.getFood()), 4);
//            result.updateBadge(String.valueOf(imageListInfo.getNature()), 5);
//            result.updateBadge(String.valueOf(imageListInfo.getObject()), 6);
//            result.updateBadge(String.valueOf(imageListInfo.getPeople()), 7);
//            result.updateBadge(String.valueOf(imageListInfo.getTechnology()), 8);
//            result.updateBadge(String.valueOf(imageListInfo.getOther()+imageListInfo.getFeatured()), 9);
//        }
//    }



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

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
        ContentFragment contentFragment = ContentFragment.newInstance(this.res);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return contentFragment;
    }

    public void switchActionBarMenu(int category){
        mNowCategory = category;
        invalidateOptionsMenu();
    }

    public interface OnFilterChangedListener {
        void onFilterChanged(int filter);
    }
}
