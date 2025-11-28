package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

public class BaiHat implements Serializable {

    private String idBH;
    private String tenBH;
    private String tenCaSi;
    private String hinhAnh;
    private String linkBH;
    private String theLoai;
    private String tenBH_lowercase;
    // Thêm trường lượt xem
    private int luotXem = 0;

    public BaiHat() {
        // Constructor rỗng bắt buộc cho Firestore
    }

    // Constructor đầy đủ
    public BaiHat(String idBH, String tenBH, String tenCaSi, String hinhAnh, String linkBH, String theLoai, String tenBH_lowercase, int luotXem) {
        this.idBH = idBH;
        this.tenBH = tenBH;
        this.tenCaSi = tenCaSi;
        this.hinhAnh = hinhAnh;
        this.linkBH = linkBH;
        this.theLoai = theLoai;
        this.tenBH_lowercase = tenBH_lowercase;
        this.luotXem = luotXem;
    }

    // --- Getters and Setters ---

    public String getIdBH() { return idBH; }
    public void setIdBH(String idBH) { this.idBH = idBH; }

    public String getTenBH() { return tenBH; }
    public void setTenBH(String tenBH) { this.tenBH = tenBH; }

    public String getTenCaSi() { return tenCaSi; }
    public void setTenCaSi(String tenCaSi) { this.tenCaSi = tenCaSi; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getLinkBH() { return linkBH; }
    public void setLinkBH(String linkBH) { this.linkBH = linkBH; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }

    public String getTenBH_lowercase() { return tenBH_lowercase; }
    public void setTenBH_lowercase(String tenBH_lowercase) { this.tenBH_lowercase = tenBH_lowercase; }

    // Getter & Setter cho luotXem
    public int getLuotXem() { return luotXem; }
    public void setLuotXem(int luotXem) { this.luotXem = luotXem; }
}
