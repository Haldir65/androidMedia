package com.me.harris.droidmedia.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class SubUserInfo : UserInfo,Parcelable {

    var records:Array<String>? = null


    constructor(name:String?, age:Long, affiliates: ArrayList<String>?): super(name,age,affiliates){

    }

    constructor(name:String?, age:Long, affiliates: ArrayList<String>?,records:Array<String>?):this(name, age, affiliates){
        this.records = records
    }

    constructor(parcel: Parcel) : this(parcel.readString(),parcel.readLong(), parcel.createStringArrayList()
    ){
        records = parcel.createStringArray()
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeStringArray(records)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubUserInfo> {
        override fun createFromParcel(parcel: Parcel): SubUserInfo {
            return SubUserInfo(parcel)
        }

        override fun newArray(size: Int): Array<SubUserInfo?> {
            return arrayOfNulls(size)
        }
    }


}