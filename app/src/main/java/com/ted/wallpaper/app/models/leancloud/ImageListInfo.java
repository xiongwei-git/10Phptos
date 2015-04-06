package com.ted.wallpaper.app.models.leancloud;

import java.util.Date;

/**
 * Created by Ted on 2015/4/4.
 */
public class ImageListInfo {
    private int all = 0;
    private int building = 0;
    private int other = 0;
    private int featured = 0;
    private int nature = 0;
    private int food = 0;
    private int technology = 0;
    private int people = 0;
    private int object = 0;

    transient private Date newPhotoUpdateTime;




    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getNature() {
        return nature;
    }

    public void setNature(int nature) {
        this.nature = nature;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getTechnology() {
        return technology;
    }

    public void setTechnology(int technology) {
        this.technology = technology;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getObject() {
        return object;
    }

    public void setObject(int object) {
        this.object = object;
    }

    public Date getNewPhotoUpdateTime() {
        return newPhotoUpdateTime;
    }

    public void setNewPhotoUpdateTime(Date newPhotoUpdateTime) {
        this.newPhotoUpdateTime = newPhotoUpdateTime;
    }
}
