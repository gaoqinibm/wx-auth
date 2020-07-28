package com.istudy.auth.controller;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

/**
 * 授权中转站
 */
@Controller
public class WxAuthController {

	@Value("${wx.appid}")
	private String appid;
	
	@Value("${wx.appsecret}")
	private String appsecret;

	@Value("${wx.callback}")
	private String callback;
	
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 授权验证
	 * @param targetUrl
	 * @return
	 * @throws IOException
	 */
	@GetMapping("wxlogin")
	public String wxAuth(String targetUrl) throws IOException {
		String http = callback + "?targetUrl=" + URLEncoder.encode(targetUrl, "UTF-8");
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid +
                "&redirect_uri=" + URLEncoder.encode(http, "UTF-8") +
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state=STATE#wechat_redirect";
		return "redirect:" + url;
	}

	/**
	 * TX 回调
	 * @param code
	 * @param targetUrl
	 * @return
	 * @throws IOException
	 */
	@GetMapping("wxcallback")
	public String wxCallback(String code, String targetUrl) throws IOException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid +
                "&secret=" + appsecret +
                "&code=" + code +
                "&grant_type=authorization_code";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		JSONObject result = JSONObject.parseObject(response.getBody());

		String openId = result.getString("openid");
        String accessToken = result.getString("access_token");

        url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken +
                "&openid=" + openId +
                "&lang=zh_CN";
        response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        result = JSONObject.parseObject(response.getBody());

		String[] strArr= targetUrl.split("\\?ref=");
		String to = strArr[0] + "?ref=";
		String rString = "/";
		if (strArr.length > 1) {
			rString = strArr[1];
		}
		to = to + URLEncoder.encode(rString, "UTF-8") +
				"&openid=" + result.getString("openid") +
				"&unionid=" + result.getString("unionid") +
				"&headimgurl=" + result.getString("headimgurl") +
				"&nickname=" + result.getString("nickname");
		return "redirect:" + to;
	}
}
