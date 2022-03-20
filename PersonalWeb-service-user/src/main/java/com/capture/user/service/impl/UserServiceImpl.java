package com.capture.user.service.impl;

import com.capture.enums.Sex;
import com.capture.enums.UserStatus;
import com.capture.exception.GraceException;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.pojo.AppUser;
import com.capture.pojo.bo.UpdateUserInfoBO;
import com.capture.pojo.vo.PublisherVO;
import com.capture.user.mapper.AppUserMapper;
import com.capture.user.mapper.AppUserMapperCustom;
import com.capture.user.service.UserService;
import com.capture.utils.DateUtil;
import com.capture.utils.DesensitizationUtil;
import com.capture.utils.JsonUtils;
import com.capture.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public AppUserMapper appUserMapper;

    @Autowired
    public AppUserMapperCustom appUserMapperCustom;

    @Autowired
    public Sid sid;

    @Autowired
    public RedisOperator redis;
    public static final String REDIS_USER_INFO = "redis_user_info";

    @Value("${userFace.temp-face}")
    public String userTempFace;


    @Override
    public AppUser queryEmailIsExist(String email) {

        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("email", email);
        AppUser user = appUserMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional
    @Override
    public AppUser createUser(String email) {
        /**
         * 互联网项目都要考虑可扩展性
         * 如果未来的业务激增，那么就需要分库分表
         * 那么数据库表主键id必须保证全局（全库）唯一，不得重复
         */
        String userId = sid.nextShort();

        AppUser user = new AppUser();



        user.setId(userId);
        user.setEmail(email);
        user.setMobile("123456");
        user.setNickname("用户:" + DesensitizationUtil.commonDisplay(email));
        user.setFace(userTempFace);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);

        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        String userId = updateUserInfoBO.getId();
        // 保证双写一致，先删除redis中的数据，后更新数据库
        redis.del(REDIS_USER_INFO + ":" + userId);

        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, userInfo);

        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);

        int result = appUserMapper.updateByPrimaryKeySelective(userInfo);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

        // 再次查询用户的最新信息，放入redis中
        AppUser user = getUser(userId);
        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));

        // 缓存双删策略
        try {
            Thread.sleep(100);
            redis.del(REDIS_USER_INFO + ":" + userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PublisherVO>  getUserList(List<String> userIdList) {

        Map<String, Object> map = new HashMap<>();
        map.put("userIdList", userIdList);
        List<PublisherVO> publisherList = appUserMapperCustom.getUserList(map);

        return publisherList;
    }
}
