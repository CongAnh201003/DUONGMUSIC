package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

public class BaiHatYT implements Serializable {
    private int id_bh;
    private int id_nd;
    private String tenBaiHat;
    private String caSi;
    private String hinhAnh;
    private String linkBaiHat;
    public BaiHatYT(){}

    public BaiHatYT(int id_bh, int id_nd, String tenBaiHat, String caSi, String hinhAnh, String linkBaiHat) {
        this.id_bh = id_bh;
        this.id_nd = id_nd;
        this.tenBaiHat = tenBaiHat;
        this.caSi = caSi;
        this.hinhAnh = hinhAnh;
        this.linkBaiHat = linkBaiHat;
    }

    public int getId_bh() {
        return id_bh;
    }

    public void setId_bh(int id_bh) {
        this.id_bh = id_bh;
    }

    public int getId_nd() {
        return id_nd;
    }

    public void setId_nd(int id_nd) {
        this.id_nd = id_nd;
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
        this.linkBaiHat = linkBaiHat;
    }
}
