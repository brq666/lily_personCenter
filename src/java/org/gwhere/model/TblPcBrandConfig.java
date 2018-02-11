package org.gwhere.model;

import javax.persistence.*;
import java.io.*;
import java.util.Date;
import java.util.List;

@Table(name = "tbl_pc_brand_config")
public class TblPcBrandConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long brandId;

    private String item;

    private String value;

    private Integer status;

    private Date createTime;

    private String createUser;

    private Date lastUpdateTime;

    private String lastUpdateUser;

    @Transient
    private List<TblPcResPicture> picList;

    @Transient
    private List<TblPcResRichtext> richList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public List<TblPcResPicture> getPicList() {
        return picList;
    }

    public void setPicList(List<TblPcResPicture> picList) {
        this.picList = picList;
    }

    public List<TblPcResRichtext> getRichList() {
        return richList;
    }

    public void setRichList(List<TblPcResRichtext> richList) {
        this.richList = richList;
    }

    public TblPcBrandConfig clone() {
        TblPcBrandConfig config = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(this);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            config = (TblPcBrandConfig) objIn.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Clone Object failed in IO.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found.", e);
        }
        return config;
    }
}