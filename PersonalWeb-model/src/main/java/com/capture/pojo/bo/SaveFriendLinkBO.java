package com.capture.pojo.bo;

import com.capture.validate.CheckUrl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SaveFriendLinkBO {
    private String id;
    @NotBlank(message = "友情链接名不能为空")
    private String linkName;
    @NotBlank(message = "友情链接地址不能为空")
    @CheckUrl
    private String linkUrl;
    @NotNull(message = "请选择保留或删除")
    private Integer isDelete;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

}