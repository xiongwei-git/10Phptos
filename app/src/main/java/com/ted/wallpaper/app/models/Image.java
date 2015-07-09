package com.ted.wallpaper.app.models;

import android.support.v7.graphics.Palette;

import com.ted.wallpaper.app.other.Constants;
import com.ted.wallpaper.app.utils.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image implements Serializable {

    private static final DateFormat sdf = SimpleDateFormat.getDateInstance();

    private String color;
    private String image_src;
    private String author;
    private String updatedAt;
    private Date date;
    private Date modified_date;
    private float ratio;
    private int width;
    private int height;
    private int featured;
    private int category;
    private String server_type;

    transient private Palette.Swatch swatch;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUrl() {
        return "http://" + image_src;
    }

    public String getHighResImage(int minWidth, int minHeight) {
        if (getServerType() == Constants.SERVER_TYPE_QINIU) {
            return getQiNiuHighResImage(minWidth, minHeight);
        }
        String url = image_src + "?fm=png";
        if (minWidth > 0 && minHeight > 0) {
            float phoneRatio = (1.0f * minWidth) / minHeight;
            if (phoneRatio < getRatio()) {
                if (minHeight < 1080) {
                    //url = url + "&h=" + minHeight;
                    url = url + "&h=" + 1080;
                }
            } else {
                if (minWidth < 1920) {
                    //url = url + "&w=" + minWidth;
                    url = url + "&w=" + 1920;
                }
            }
        }

        return url;
    }

    public String getQiNiuHighResImage(int minWidth, int minHeight) {
        String url = "http://" + image_src;//h.1080;
        if (minWidth > 0 && minHeight > 0) {
            float phoneRatio = (1.0f * minWidth) / minHeight;
            if (phoneRatio < getRatio()) {
                if (minHeight < 1080) {
                    url = url + "/fm.png." + "h." + 1080;
                }
            } else {
                if (minWidth < 1920) {
                    url = url + "/fm.png." + "w." + 1920;
                }
            }
        }
        return url;
    }


    public String getImageSrc(int screenWidth) {
        if (getServerType() == Constants.SERVER_TYPE_QINIU) {
            return getQiNiuImageSrc(screenWidth);
        }
        return image_src + "?q=75&fm=jpg&w=" + Utils.optimalImageWidth(screenWidth);

        /*
        wait with this one for now. i don't want to bring up the generation quota of unsplash
        String url = image_src + "?q=75&fit=max&fm=jpg";

        if (screenWidth > 0) {
            //it's enough if we load an image with 2/3 of the size
            url = url + "&w=" + (screenWidth / 3 * 2);
        }

        return url;
        */
    }

    public String getQiNiuImageSrc(int screenWidth) {
        return "http://" + image_src + "/q75.w" + Utils.optimalImageWidth(screenWidth);
    }

    public void setImageSrc(String image_src) {
        this.image_src = image_src;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public String getReadableDate() {
        if (date != null) {
            return sdf.format(date);
        } else {
            return "";
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getModified_date() {
        return modified_date;
    }

    public String getReadableModified_Date() {
        if (modified_date != null) {
            return sdf.format(modified_date);
        } else {
            return "";
        }
    }

    public void setModified_date(Date modified_date) {
        this.modified_date = modified_date;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Palette.Swatch getSwatch() {
        return swatch;
    }

    public void setSwatch(Palette.Swatch swatch) {
        this.swatch = swatch;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getServer_type() {
        return server_type;
    }

    public void setServer_type(String server_type) {
        this.server_type = server_type;
    }

    public int getServerType() {
        if ("qiniu".equalsIgnoreCase(getServer_type())) {
            return Constants.SERVER_TYPE_QINIU;
        }
        return Constants.SERVER_TYPE_IMGIX;
    }
}
