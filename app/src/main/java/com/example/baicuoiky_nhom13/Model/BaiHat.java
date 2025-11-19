package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

// Đảm bảo class implements Serializable để có thể truyền qua Intent
public class BaiHat implements Serializable {

    // Các thuộc tính phải khớp với tên trường trên Firestore
    private String idBH;       // ID của bài hát (sẽ là ID của document)
    private String tenBH;      // Tên bài hát
    private String tenCaSi;    // Tên ca sĩ
    private String hinhAnh;    // URL hình ảnh
    private String linkBH;     // URL file nhạc
    private String theLoai;    // Thể loại
    private String tenBH_lowercase; // Trường dùng để tìm kiếm

    // Constructor rỗng là BẮT BUỘC để Firestore có thể tự động chuyển đổi
    public BaiHat() {
    }

    // --- Getters and Setters (BẮT BUỘC) ---

    public String getIdBH() {
        return idBH;
    }

    public void setIdBH(String idBH) {
        this.idBH = idBH;
    }

    public String getTenBH() {
        return tenBH;
    }

    public void setTenBH(String tenBH) {
        this.tenBH = tenBH;
    }

    public String getTenCaSi() {
        return tenCaSi;
    }

    public void setTenCaSi(String tenCaSi) {
        this.tenCaSi = tenCaSi;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getLinkBH() {
        return linkBH;
    }

    public void setLinkBH(String linkBH) {
        this.linkBH = linkBH;
    }

    public String getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(String theLoai) {
        this.theLoai = theLoai;
    }

    public String getTenBH_lowercase() {
        return tenBH_lowercase;
    }

    public void setTenBH_lowercase(String tenBH_lowercase) {
        this.tenBH_lowercase = tenBH_lowercase;
    }
}
