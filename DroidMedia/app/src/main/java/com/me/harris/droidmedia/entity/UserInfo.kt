package com.me.harris.droidmedia.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

open class UserInfo() :Parcelable{

//    constructor(name:String):this(){
//        this.name = name
//    }

    constructor(name:String,age:Long,affs:ArrayList<String>):this(){
        this.name = name
        this.age = age;
        this.affilates = affs
    }

    var name:String? = null
    var age :Long = 0L
    var affilates:ArrayList<String>? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        age = parcel.readLong()
        affilates = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(age)
        parcel.writeStringList(affilates)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }


}

