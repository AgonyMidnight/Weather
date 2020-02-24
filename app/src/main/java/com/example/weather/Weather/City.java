package com.example.weather.Weather;

public class City {
    public String NameCity;

    public City(){}

    public String getNameCity() {
        return NameCity;
    }

    public void setNameCity(String nameCity) {
        NameCity = nameCity;
    }


    @Override
    public String toString() {
        return new StringBuilder("[").append(this.NameCity).append(']').toString();
    }
}
