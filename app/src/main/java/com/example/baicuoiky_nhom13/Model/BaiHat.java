package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

public class BaiHat implements Serializable {
    private int id;
    private String tenBaiHat;
    private String caSi;
    private String hinhAnh;
    private String linkBaiHat;
    public BaiHat(){}

    public BaiHat(int id, String tenBaiHat, String caSi, String hinhAnh, String linkBaiHat) {
        this.id = id;
        this.tenBaiHat = tenBaiHat;
        this.caSi = caSi;
        this.hinhAnh = hinhAnh;
        this.linkBaiHat = linkBaiHat;
    }

    @Override
    public String toString() {
        return "BaiHat{" +
                "id=" + id +
                ", tenBaiHat='" + tenBaiHat + '\'' +
                ", caSi='" + caSi + '\'' +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", linkBaiHat='" + linkBaiHat + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenBaiHat() {
        return tenBaiHat;
    }

    public void setTenBaiHat(String tenBaiHat) {
        this.tenBaiHat = tenBaiHat;
    }

    public String getCaSi() {
        return caSi;
    }

    public void setCaSi(String caSi) {
        this.caSi = caSi;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getLinkBaiHat() {
        return linkBaiHat;
    }

    public void setLinkBaiHat(String linkBaiHat) {
        this.linkBaiHat = linkBaiHat;}
}
