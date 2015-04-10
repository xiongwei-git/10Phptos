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

    public SlideMenuItem() {
    }

    public SlideMenuItem(String name, int imageRes) {
        this.name = name;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public SlideMenuItem setName(String name) {
        this.name = name;
        return this;
    }

    public int getImageRes() {
        return imageRes;
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
