package com.medi.common_protobuf.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lixiang on 2021/2/18
 * Describe:
 */
public class ParcelableBean implements Parcelable {
    private int age;
    private String name;

    protected ParcelableBean(Parcel in) {
        age = in.readInt();
        name = in.readString();
    }

    public static final Creator<ParcelableBean> CREATOR = new Creator<ParcelableBean>() {
        @Override
        public ParcelableBean createFromParcel(Parcel in) {
            return new ParcelableBean(in);
        }

        @Override
        public ParcelableBean[] newArray(int size) {
            return new ParcelableBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(age);
        parcel.writeString(name);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
