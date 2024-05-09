package com.mkandeel.correctsoc;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Model implements Parcelable {
    private String appName;
    private List<String> permission;

    public Model(String appName, List<String> permission) {
        this.appName = appName;
        this.permission = permission;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeStringList(this.permission);
    }

    public void readFromParcel(Parcel source) {
        this.appName = source.readString();
        this.permission = source.createStringArrayList();
    }

    protected Model(Parcel in) {
        this.appName = in.readString();
        this.permission = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Model> CREATOR = new Parcelable.Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel source) {
            return new Model(source);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };
}
