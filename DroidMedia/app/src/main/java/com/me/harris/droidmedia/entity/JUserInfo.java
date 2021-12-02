package com.me.harris.droidmedia.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class JUserInfo implements Parcelable {

    public String name;
     long age;
     List<String> affs;

    public JUserInfo(){
    }

    public JUserInfo(String name,long age,List<String> affs){
        this();
        this.name = name;
        this.age = age;
        this.affs = affs;
    }

    protected JUserInfo(Parcel in) {
        name = in.readString();
        age = in.readLong();
        affs = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(age);
        dest.writeStringList(affs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JUserInfo> CREATOR = new Creator<JUserInfo>() {
        @Override
        public JUserInfo createFromParcel(Parcel in) {
            return new JUserInfo(in);
        }

        @Override
        public JUserInfo[] newArray(int size) {
            return new JUserInfo[size];
        }
    };
}
