package com.capture.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.capture.utils.extend.AliyunResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSUtils {

    @Autowired
    public AliyunResource aliyunResource;

    public void sendSMS(String mobile, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                                                aliyunResource.getAccessKeyID(),
                                                aliyunResource.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com"); //固定
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");//写死
        request.putQueryParameter("RegionId", "cn-hangzhou");

        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "capture");
        request.putQueryParameter("TemplateCode", "SMS_183761535");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
