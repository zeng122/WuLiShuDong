package com.example.wulishudong.bean;

import java.io.File;

public class UserInfo {
  private int id;
  private String name;
  private String password;
  private int sex;
  private String signature;
  private String telephone;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getSex() {
    return sex;
  }

  public String getSignature() {
    return signature;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setSex(int sex) {
    this.sex = sex;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }
}