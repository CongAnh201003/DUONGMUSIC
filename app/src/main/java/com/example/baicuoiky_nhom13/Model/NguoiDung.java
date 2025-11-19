package com.example.baicuoiky_nhom13.Model;

import java.io.Serializable;

public class NguoiDung implements Serializable {    private String id;
    private String hoTen;
    private String email;
    private String matKhau;
    private String vaiTro;
    private String anhDaiDien; // <<<=== THÊM TRƯỜNG MỚI NÀY

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public NguoiDung() {
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    // <<<=== THÊM GETTER VÀ SETTER MỚI NÀY ===>>>
    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }
}
