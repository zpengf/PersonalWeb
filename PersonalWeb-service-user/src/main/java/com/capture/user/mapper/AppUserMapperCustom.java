package com.capture.user.mapper;

import com.capture.my.mapper.MyMapper;
import com.capture.pojo.AppUser;
import com.capture.pojo.vo.PublisherVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AppUserMapperCustom {

    public List<PublisherVO> getUserList(Map<String, Object> map);

}