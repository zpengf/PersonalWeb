package com.capture.user.controller;

import com.capture.api.BaseController;
import com.capture.api.controller.user.MyFansControllerApi;
import com.capture.enums.Sex;
import com.capture.grace.result.GraceJSONResult;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.pojo.vo.FansCountsVO;
import com.capture.user.service.MyFanService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {

    final static Logger logger = LoggerFactory.getLogger(MyFansController.class);

    @Autowired
    private MyFanService myFanService;

    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId,
                                                String fanId) {
        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)){

            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EXIST_ERROR);

        }

        boolean res = myFanService.isMeFollowThisWriter(writerId, fanId);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {

        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)){

            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EXIST_ERROR);

        }

        myFanService.follow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        myFanService.unfollow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId,
                                    Integer page,
                                    Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        return GraceJSONResult.ok(myFanService.queryMyFansList(writerId,
                                    page,
                                    pageSize));
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {

        int manCounts = myFanService.queryFansCounts(writerId, Sex.man);
        int womanCounts = myFanService.queryFansCounts(writerId, Sex.woman);

        FansCountsVO fansCountsVO = new FansCountsVO();
        fansCountsVO.setManCounts(manCounts);
        fansCountsVO.setWomanCounts(womanCounts);

        return GraceJSONResult.ok(fansCountsVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(String writerId) {
        return GraceJSONResult.ok(myFanService
                .queryRegionRatioCounts(writerId));
    }
}
