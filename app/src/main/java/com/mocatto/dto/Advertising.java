package com.mocatto.dto;

/**
 * Created by froilan.ruiz on 9/29/2016.
 */
public class Advertising {
    private Integer id;
    private String name;
    private Integer timesToShow;
    private Integer timesShowed;
    private String image;
    private Boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTimesToShow() {
        return timesToShow;
    }

    public void setTimesToShow(Integer timesToShow) {
        this.timesToShow = timesToShow;
    }

    public Integer getTimesShowed() {
        return timesShowed;
    }

    public void setTimesShowed(Integer timesShowed) {
        this.timesShowed = timesShowed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Advertising{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timesToShow=" + timesToShow +
                ", timesShowed=" + timesShowed +
                ", image='" + image + '\'' +
                ", active=" + active +
                '}';
    }
}
