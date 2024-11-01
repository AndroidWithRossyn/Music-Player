package com.demo.music.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MPMEqualizerModel implements Parcelable {
    public static final Creator<MPMEqualizerModel> CREATOR = new Creator<MPMEqualizerModel>() { 
        
        @Override 
        public MPMEqualizerModel createFromParcel(Parcel parcel) {
            return new MPMEqualizerModel(parcel);
        }

        
        @Override 
        public MPMEqualizerModel[] newArray(int i) {
            return new MPMEqualizerModel[i];
        }
    };
    private int bassProgress;
    private short bassStrength;
    private int effectId;
    private String effectNAME;
    private boolean isEqualizerEnabled;
    private int presetPos;
    private short reverbPreset;
    private int reverbProgress;
    private int[] seekbarpos;

    @Override 
    public int describeContents() {
        return 0;
    }

    public MPMEqualizerModel() {
        this.seekbarpos = new int[5];
        this.isEqualizerEnabled = true;
        this.reverbPreset = (short) -1;
        this.bassStrength = (short) -1;
    }

    protected MPMEqualizerModel(Parcel parcel) {
        this.seekbarpos = new int[5];
        this.isEqualizerEnabled = parcel.readByte() != 0;
        this.seekbarpos = parcel.createIntArray();
        this.presetPos = parcel.readInt();
        this.reverbPreset = (short) parcel.readInt();
        this.bassStrength = (short) parcel.readInt();
    }

    public boolean isEqualizerEnabled() {
        return this.isEqualizerEnabled;
    }

    public void setEqualizerEnabled(boolean z) {
        this.isEqualizerEnabled = z;
    }

    public int[] getSeekbarpos() {
        return this.seekbarpos;
    }

    public void setSeekbarpos(int[] iArr) {
        this.seekbarpos = iArr;
    }

    public int getPresetPos() {
        return this.presetPos;
    }

    public void setPresetPos(int i) {
        this.presetPos = i;
    }

    public short getReverbPreset() {
        return this.reverbPreset;
    }

    public void setReverbPreset(short s) {
        this.reverbPreset = s;
    }

    public short getBassStrength() {
        return this.bassStrength;
    }

    public void setBassStrength(short s) {
        this.bassStrength = s;
    }

    public int getEffectId() {
        return this.effectId;
    }

    public void setEffectId(int i) {
        this.effectId = i;
    }

    public String getEffectNAME() {
        return this.effectNAME;
    }

    public void setEffectNAME(String str) {
        this.effectNAME = str;
    }

    public int getBassProgress() {
        return this.bassProgress;
    }

    public void setBassProgress(int i) {
        this.bassProgress = i;
    }

    public int getReverbProgress() {
        return this.reverbProgress;
    }

    public void setReverbProgress(int i) {
        this.reverbProgress = i;
    }

    @Override 
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.isEqualizerEnabled ? (byte) 1 : (byte) 0);
        parcel.writeIntArray(this.seekbarpos);
        parcel.writeInt(this.presetPos);
        parcel.writeInt(this.reverbPreset);
        parcel.writeInt(this.bassStrength);
        parcel.writeInt(this.effectId);
        parcel.writeInt(this.reverbProgress);
        parcel.writeInt(this.bassProgress);
        parcel.writeString(this.effectNAME);
    }
}
