package com.capture.user.service;

import com.capture.pojo.AppUser;
import com.capture.pojo.bo.UpdateUserInfoBO;
import com.capture.pojo.vo.PublisherVO;

import java.util.List;

public interface UserService {

    /**
     * 判断用户是否存在，如果存在返回user信息
     */
    public AppUser queryEmailIsExist(String mobile);

    /**
     * 创建用户，新增用户记录到数据库
     */
    public AppUser createUser(String email);

    /**
     * 根据用户主键id查询用户信息
     */
    public AppUser getUser(String userId);

    /**
     * 用户修改信息，完善资料，并且激活
     */
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);
//
//    /**
//     * 根据用户id查询用户
//     */
//    public List<PublisherVO> getUserList(List<String> userIdList);
}
