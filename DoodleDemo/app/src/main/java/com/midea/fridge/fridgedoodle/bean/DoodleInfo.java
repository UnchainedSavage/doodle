package com.midea.fridge.fridgedoodle.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenxiaofei on 2016/12/31.
 */
public class DoodleInfo implements Parcelable {
    private String name;
    private String savePath;
    private String imagePath;
    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(savePath);
        dest.writeString(imagePath);
        dest.writeLong(timestamp);
    }

    public static final Parcelable.Creator<DoodleInfo> CREATOR = new Creator<DoodleInfo>() {

        @Override
        public DoodleInfo createFromParcel(Parcel source) {
            DoodleInfo doodleInfo = new DoodleInfo();
            doodleInfo.setName(source.readString());
            doodleInfo.setSavePath(source.readString());
            doodleInfo.setImagePath(source.readString());
            doodleInfo.setTimestamp(source.readLong());
            return doodleInfo;
        }

        @Override
        public DoodleInfo[] newArray(int size) {
            return new DoodleInfo[size];
        }
    };
}
