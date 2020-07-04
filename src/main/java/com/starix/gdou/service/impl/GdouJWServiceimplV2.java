package com.starix.gdou.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starix.gdou.dto.LoginResultV2;
import com.starix.gdou.dto.request.ExamQueryRquestDTO;
import com.starix.gdou.dto.request.ScoreQueryRquestDTO;
import com.starix.gdou.dto.response.ExamQueryResponseDTO;
import com.starix.gdou.dto.response.ScoreQueryResponseDTO;
import com.starix.gdou.exception.CustomException;
import com.starix.gdou.response.CommonResult;
import com.starix.gdou.service.GdouJWServiceV2;
import com.starix.gdou.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 适配新版教务系统的service实现类
 * @author shiwenjie
 * @created 2020/6/28 5:16 下午
 **/
@Service
@Slf4j
public class GdouJWServiceimplV2 implements GdouJWServiceV2 {

    //教务系统地址
    @Value("${gdou.webvpn-url}")
    private String WEBVPN_URL;
    @Value("${gdou.jw-url}")
    private String JW_URL;
    @Value("${gdou.webvpn-username}")
    private String WEBVPN_USERNAME;
    @Value("${gdou.webvpn-password}")
    private String WEBVPN_PASSWORD;


    @Override
    public LoginResultV2 login(String useranme, String password) throws Exception {
        doWebvpnLogin();
        // 获取publickey，用于密码rsa加密
        String resultStr = HttpClientUtil.doGet(JW_URL + "/xtgl/login_getPublicKey.html");
        JSONObject json = JSON.parseObject(resultStr);
        // TODO: 2020-07-05 RSA密码加密待实现
        System.out.println(resultStr);
        return null;
    }


    /**
     * 登录webvpn，获取cookie（HttpClient会自己维护cookie）
     * @throws Exception
     */
    private void doWebvpnLogin() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("auth_type", "local");
        params.put("username", WEBVPN_USERNAME);
        params.put("sms_code", "");
        params.put("password", WEBVPN_PASSWORD);
        params.put("needCaptcha", "false");
        String resultStr = HttpClientUtil.doPost(WEBVPN_URL + "/do-login?local_login=true", params);
        JSONObject json = JSON.parseObject(resultStr);
        if (!json.getBoolean("success")){
            if ("NEED_CONFIRM".equals(json.getString("error"))){
                // 当前需要确认登录
                log.info("踢掉已登录的webvpn客户端");
                doWebvpnConfirmLogin();
            }else {
                log.error("内置webvpn异常：{}", resultStr);
                throw new CustomException(CommonResult.failed("内置webvpn异常，等待修复"));
            }
        }
    }

    /**
     * 执行确认登录，踢掉已登录的webvpn客户端
     */
    private void doWebvpnConfirmLogin() throws IOException {
        String resultStr = HttpClientUtil.doPost(WEBVPN_URL + "/do-confirm-login", new HashMap<>());
        JSONObject json = JSON.parseObject(resultStr);
        if (!json.getBoolean("success")){
            throw new CustomException(CommonResult.failed("内置webvpn异常，等待修复"));
        }
    }

    @Override
    public LoginResultV2 loginByOpenid(String openid) {
        return null;
    }

    @Override
    public ScoreQueryResponseDTO queryScore(ScoreQueryRquestDTO scoreQueryRquestDTO) {
        return null;
    }

    @Override
    public ExamQueryResponseDTO queryExam(ExamQueryRquestDTO examQueryRquestDTO) {
        return null;
    }

    @Override
    public List<String> getSocreYearOptionsList(String cookie) throws Exception {
        return null;
    }

    @Override
    public List<String> getExamYearOptionsList(String cookie) throws Exception {
        return null;
    }
}