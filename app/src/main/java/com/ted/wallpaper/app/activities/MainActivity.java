package com.ted.wallpaper.app.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.avos.avoscloud.AVAnalytics;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.ted.wallpaper.app.R;
import com.ted.wallpaper.app.models.leancloud.ImageListInfo;
import com.ted.wallpaper.app.utils.Utils;


public class MainActivity extends ActionBarActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.category_new_10).withIdentifier(Category.NEW.id).withIcon(GoogleMaterial.Icon.gmd_filter_9_plus),
                        new PrimaryDrawerItem().withName(R.string.category_all).withIdentifier(Category.ALL.id).withIcon(GoogleMaterial.Icon.gmd_perm_media),
                        //new PrimaryDrawerItem().withName(R.string.category_loved).withIdentifier(Category.LOVED.id).withIcon(GoogleMaterial.Icon.gmd_favorite),
                        new SectionDrawerItem().withName(R.string.category_section_categories),
                        new PrimaryDrawerItem().withName(R.string.category_buildings).withIdentifier(Category.BUILDINGS.id).withIcon(GoogleMaterial.Icon.gmd_location_city),
                        new PrimaryDrawerItem().withName(R.string.category_food).withIdentifier(Category.FOOD.id).withIcon(GoogleMaterial.Icon.gmd_local_bar),
                        new PrimaryDrawerItem().withName(R.string.category_nature).withIdentifier(Category.NATURE.id).withIcon(GoogleMaterial.Icon.gmd_local_florist),
                        new PrimaryDrawerItem().withName(R.string.category_objects).withIdentifier(Category.OBJECTS.id).withIcon(GoogleMaterial.Icon.gmd_style),
                        new PrimaryDrawerItem().withName(R.string.category_people).withIdentifier(Category.PEOPLE.id).withIcon(GoogleMaterial.Icon.gmd_person),
                        new PrimaryDrawerItem().withName(R.string.category_technology).withIdentifier(Category.TECHNOLOGY.id).withIcon(GoogleMaterial.Icon.gmd_local_see),
                        new PrimaryDrawerItem().withName(R.string.category_other).withIdentifier(Category.OTHER.id).withIcon(GoogleMaterial.Icon.gmd_loyalty)
        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem instanceof Nameable) {
                                toolbar.setTitle(((Nameable) drawerItem).getNameRes());
                            }
                            if (onFilterChangedListener != null) {
                                onFilterChangedListener.onFilterChanged(drawerItem.getIdentifier());
                            }
                        }
                    }
                })
                .build();

        //disable scrollbar :D it's ugly
        result.getListView().setVerticalScrollBarEnabled(false);
    }

//    /**
//     * @param images
//     */
//    public void setCategoryCount(ImageResults images) {
//        if (result.getDrawerItems() != null && result.getDrawerItems().size() == 9 && images != null && images.getResults() != null) {
//            result.updateBadge(images.getResults().size() + "", 0);
//            result.updateBadge(LeanCloudApi.countFeatured(images.getResults()) + "", 1);
//
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.BUILDINGS.id) + "", 3);
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.FOOD.id) + "", 4);
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.NATURE.id) + "", 5);
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.OBJECTS.id) + "", 6);
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.PEOPLE.id) + "", 7);
//            result.updateBadge(LeanCloudApi.countCategory(images.getResults(), Category.TECHNOLOGY.id) + "", 8);
//        }
//    }

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
    /***
     * 更新分类信息
     * @param imageListInfo
     */
    public void updateImageCategoryInfo(ImageListInfo imageListInfo) {
        if (result.getDrawerItems() != null && result.getDrawerItems().size() == 10 && imageListInfo != null) {
            Resources res = getResources();
            String updateTime = Utils.getFormatDateStr(imageListInfo.getNewPhotoUpdateTime());
            updateTime = TextUtils.isEmpty(updateTime)?res.getString(R.string.update_time_default):res.getString(R.string.update_time_auto,updateTime);
            result.updateBadge(updateTime, 0);
            result.updateBadge(String.valueOf(imageListInfo.getAll()), 1);

            result.updateBadge(String.valueOf(imageListInfo.getBuilding()), 3);
            result.updateBadge(String.valueOf(imageListInfo.getFood()), 4);
            result.updateBadge(String.valueOf(imageListInfo.getNature()), 5);
            result.updateBadge(String.valueOf(imageListInfo.getObject()), 6);
            result.updateBadge(String.valueOf(imageListInfo.getPeople()), 7);
            result.updateBadge(String.valueOf(imageListInfo.getTechnology()), 8);
            result.updateBadge(String.valueOf(imageListInfo.getOther()+imageListInfo.getFeatured()), 9);
        }
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

    public void switchActionBarMenu(int category){
        mNowCategory = category;
        invalidateOptionsMenu();
    }

    public interface OnFilterChangedListener {
        void onFilterChanged(int filter);
    }
}
