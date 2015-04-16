package yalantis.com.sidemenu.model;

import com.mikepenz.iconics.typeface.IIcon;
import yalantis.com.sidemenu.interfaces.Resourceble;

/**
 * Created by Konstantin on 23.12.2014.
 */
public class SlideMenuItem implements Resourceble {
    private String name;
    private int imageRes;
    private IIcon iicon;
    private int itemCategoryId;

    public SlideMenuItem() {
    }

    public SlideMenuItem(String name, int imageRes) {
        this.name = name;
        this.imageRes = imageRes;
    }

    @Override
    public int getCategoryId() {
        return itemCategoryId;
    }

    @Override
    public int getImageRes() {
        return imageRes;
    }

    @Override
    public String getName() {
        return name;
    }

    public SlideMenuItem setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
        return this;
    }

    public SlideMenuItem setName(String name) {
        this.name = name;
        return this;
    }

    public SlideMenuItem setImageRes(int imageRes) {
        this.imageRes = imageRes;
        return this;
    }

    public IIcon getIicon() {
        return iicon;
    }

    public SlideMenuItem setIicon(IIcon iicon) {
        this.iicon = iicon;
        return this;
    }
}
