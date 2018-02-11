package org.gwhere.vo;

import org.gwhere.model.TblPcBrandConfig;
import org.gwhere.model.TblPcBrandMenu;
import org.gwhere.model.TblPcResPicture;

import java.util.List;
import java.util.Map;

public class BrandInfoVO {
    private String name;

    private Long id;

    private String code;

    private Integer status;

    private List<TblPcBrandMenu> menus;

    private Map<String, Object> config;

    private List<TblPcResPicture> welcomeRs;

    private List<TblPcResPicture> bannerRs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMenus(List<TblPcBrandMenu> menus) {
        this.menus = menus;
    }

    public List<TblPcBrandMenu> getMenus() {
        return menus;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public List<TblPcResPicture> getWelcomeRs() {
        return welcomeRs;
    }

    public void setWelcomeRs(List<TblPcResPicture> welcomeRs) {
        this.welcomeRs = welcomeRs;
    }

    public List<TblPcResPicture> getBannerRs() {
        return bannerRs;
    }

    public void setBannerRs(List<TblPcResPicture> bannerRs) {
        this.bannerRs = bannerRs;
    }
}
