package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

public class NguoiDung implements Serializable {
    private int id;
    private String tenDN;
    private String matKhau;
    private String hoTen;
    private String email;
    private String hinhAnh;
    private int vaiTro;// 1 quan tri vien, 2 nguoi dung

    public NguoiDung(){}

    public NguoiDung(int id, String tenDN, String matKhau, String hoTen, String email, String hinhAnh, int vaiTro) {
        this.id = id;
        this.tenDN = tenDN;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.hinhAnh = hinhAnh;
        this.vaiTro = vaiTro;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDN() {
        return tenDN;
    }

    public void setTenDN(String tenDN) {
        this.tenDN = tenDN;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(int vaiTro) {
        this.vaiTro = vaiTro;
    }
}
