package com.mocatto.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by froilan.ruiz on 6/7/2016.
 */
public class Pet implements Serializable, Parcelable {
    private Integer id;
    private String name;
    private String specie;
    private String race;
    private String gender;
    private Date birthDate;
    private String fur;
    private String diet;
    private String dietName;
    private Integer foodGrams;
    private String foodBrand;
    private String foodBrandName;
    private Integer foodBuyRegularity;
    private Date lastFoodBuyDate;
    private Date lastVaccine;
    private String vaccine;
    private String woopingCough;
    private Date woopingCoughDate;
    private Date lastWorm;
    private String antiFlea;
    private Date lastVetVisit;
    private String sterilized;
    private Integer bathFrecuency;
    private Date lastBath;
    private String bathLocation;
    private String others;
    private String event;
    private String regularity;
    private String aidsLeukemiaVaccine;
    private Date aidsLeukemiaVaccineDate;
    private String email;
    private byte[] photo;

    public Pet() {
    }

    public Pet(Integer id, String name, String specie, String race, String gender, Date birthDate, String fur, String email, byte[] photo) {
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.race = race;
        this.gender = gender;
        this.birthDate = birthDate;
        this.fur = fur;
        this.email = email;
        this.photo = photo;
    }

    public Pet(Integer id, String name, String specie, String race, String gender, Date birthDate, String fur, String diet, String dietName, Integer foodGrams, String foodBrand, String foodBrandName, Integer foodBuyRegularity, Date lastFoodBuyDate, Date lastVaccine, String vaccine, String woopingCough, Date woopingCoughDate, Date fecha_ultima_desparacitación, String antiFlea, Date lastVetVisit, String sterilized, Integer bathFrecuency, Date lastBath, String bathLocation, String others, String event, String regularity, String aidsLeukemiaVaccine, Date aidsLeukemiaVaccineDate, String email) {
        this.id = id;
        this.name = name;
        this.specie = specie;
        this.race = race;
        this.gender = gender;
        this.birthDate = birthDate;
        this.fur = fur;
        this.diet = diet;
        this.dietName = dietName;
        this.foodGrams = foodGrams;
        this.foodBrand = foodBrand;
        this.foodBrandName = foodBrandName;
        this.foodBuyRegularity = foodBuyRegularity;
        this.lastFoodBuyDate = lastFoodBuyDate;
        this.lastVaccine = lastVaccine;
        this.vaccine = vaccine;
        this.woopingCough = woopingCough;
        this.woopingCoughDate = woopingCoughDate;
        this.lastWorm = fecha_ultima_desparacitación;
        this.antiFlea = antiFlea;
        this.lastVetVisit = lastVetVisit;
        this.sterilized = sterilized;
        this.bathFrecuency = bathFrecuency;
        this.lastBath = lastBath;
        this.bathLocation = bathLocation;
        this.others = others;
        this.event = event;
        this.regularity = regularity;
        this.aidsLeukemiaVaccine = aidsLeukemiaVaccine;
        this.aidsLeukemiaVaccineDate = aidsLeukemiaVaccineDate;
        this.email = email;
    }

    protected Pet(Parcel in) {
        name = in.readString();
        specie = in.readString();
        race = in.readString();
        gender = in.readString();
        fur = in.readString();
        diet = in.readString();
        dietName = in.readString();
        foodBrand = in.readString();
        foodBrandName = in.readString();
        vaccine = in.readString();
        woopingCough = in.readString();
        antiFlea = in.readString();
        sterilized = in.readString();
        bathLocation = in.readString();
        others = in.readString();
        event = in.readString();
        regularity = in.readString();
        aidsLeukemiaVaccine = in.readString();
        email = in.readString();
        photo = in.createByteArray();
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

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

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFur() {
        return fur;
    }

    public void setFur(String fur) {
        this.fur = fur;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public Integer getFoodGrams() {
        return foodGrams;
    }

    public void setFoodGrams(Integer foodGrams) {
        this.foodGrams = foodGrams;
    }

    public String getFoodBrand() {
        return foodBrand;
    }

    public void setFoodBrand(String foodBrand) {
        this.foodBrand = foodBrand;
    }

    public Integer getFoodBuyRegularity() {
        return foodBuyRegularity;
    }

    public void setFoodBuyRegularity(Integer foodBuyRegularity) {
        this.foodBuyRegularity = foodBuyRegularity;
    }

    public Date getLastVaccine() {
        return lastVaccine;
    }

    public void setLastVaccine(Date lastVaccine) {
        this.lastVaccine = lastVaccine;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public String getWoopingCough() {
        return woopingCough;
    }

    public void setWoopingCough(String woopingCough) {
        this.woopingCough = woopingCough;
    }

    public Date getWoopingCoughDate() {
        return woopingCoughDate;
    }

    public void setWoopingCoughDate(Date woopingCoughDate) {
        this.woopingCoughDate = woopingCoughDate;
    }

    public Date getLastWorm() {
        return lastWorm;
    }

    public void setLastWorm(Date lastWorm) {
        this.lastWorm = lastWorm;
    }

    public String getAntiFlea() {
        return antiFlea;
    }

    public void setAntiFlea(String antiFlea) {
        this.antiFlea = antiFlea;
    }

    public Date getLastVetVisit() {
        return lastVetVisit;
    }

    public void setLastVetVisit(Date lastVetVisit) {
        this.lastVetVisit = lastVetVisit;
    }

    public String getSterilized() {
        return sterilized;
    }

    public void setSterilized(String sterilized) {
        this.sterilized = sterilized;
    }

    public Integer getBathFrecuency() {
        return bathFrecuency;
    }

    public void setBathFrecuency(Integer bathFrecuency) {
        this.bathFrecuency = bathFrecuency;
    }

    public Date getLastBath() {
        return lastBath;
    }

    public void setLastBath(Date lastBath) {
        this.lastBath = lastBath;
    }

    public String getBathLocation() {
        return bathLocation;
    }

    public void setBathLocation(String bathLocation) {
        this.bathLocation = bathLocation;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getRegularity() {
        return regularity;
    }

    public void setRegularity(String regularity) {
        this.regularity = regularity;
    }

    public String getAidsLeukemiaVaccine() {
        return aidsLeukemiaVaccine;
    }

    public void setAidsLeukemiaVaccine(String aidsLeukemiaVaccine) {
        this.aidsLeukemiaVaccine = aidsLeukemiaVaccine;
    }

    public Date getAidsLeukemiaVaccineDate() {
        return aidsLeukemiaVaccineDate;
    }

    public void setAidsLeukemiaVaccineDate(Date aidsLeukemiaVaccineDate) {
        this.aidsLeukemiaVaccineDate = aidsLeukemiaVaccineDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoodBrandName() {
        return foodBrandName;
    }

    public void setFoodBrandName(String foodBrandName) {
        this.foodBrandName = foodBrandName;
    }

    public Date getLastFoodBuyDate() {
        return lastFoodBuyDate;
    }

    public void setLastFoodBuyDate(Date lastFoodBuyDate) {
        this.lastFoodBuyDate = lastFoodBuyDate;
    }

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    @Override
    public String toString() {
        return "Mascota{" +
                "id="+id+ '\'' +
                ",nombre='" + name + '\'' +
                ", specie='" + specie + '\'' +
                ", race='" + race + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", fur='" + fur + '\'' +
                ", diet='" + diet + '\'' +
                ", foodGrams=" + foodGrams +
                ", foodBrand='" + foodBrand + '\'' +
                ", foodBuyRegularity=" + foodBuyRegularity +
                ", lastFoodBuyDate="+ lastFoodBuyDate +
                ", lastVaccine=" + lastVaccine +
                ", vaccine='" + vaccine + '\'' +
                ", woopingCough='" + woopingCough + '\'' +
                ", woopingCoughDate=" + woopingCoughDate +
                ", lastWorm=" + lastWorm +
                ", antiFlea='" + antiFlea + '\'' +
                ", lastVetVisit=" + lastVetVisit +
                ", sterilized='" + sterilized + '\'' +
                ", bathFrecuency=" + bathFrecuency +
                ", lastBath=" + lastBath +
                ", bathLocation='" + bathLocation + '\'' +
                ", others='" + others + '\'' +
                ", event='" + event + '\'' +
                ", regularity='" + regularity + '\'' +
                ", aidsLeukemiaVaccine='" + aidsLeukemiaVaccine + '\'' +
                ", aidsLeukemiaVaccineDate=" + aidsLeukemiaVaccineDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(specie);
        parcel.writeString(race);
        parcel.writeString(gender);
        parcel.writeString(fur);
        parcel.writeString(diet);
        parcel.writeString(dietName);
        parcel.writeString(foodBrand);
        parcel.writeString(foodBrandName);
        parcel.writeString(vaccine);
        parcel.writeString(woopingCough);
        parcel.writeString(antiFlea);
        parcel.writeString(sterilized);
        parcel.writeString(bathLocation);
        parcel.writeString(others);
        parcel.writeString(event);
        parcel.writeString(regularity);
        parcel.writeString(aidsLeukemiaVaccine);
        parcel.writeString(email);
        parcel.writeByteArray(photo);
    }
}
