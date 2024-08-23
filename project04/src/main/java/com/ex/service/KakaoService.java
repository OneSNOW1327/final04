package com.ex.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ex.data.KakaoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.json.JSONObject;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String REDIRECT_URI = "http://192.168.219.203:8080/user/kakao";
    private static final String CLIENT_ID = "2973e8a76b1a6fd676870daca0d61cc1";
    private static final String CLIENT_SECRET = "3OHnlturZOhcXWnCKEryAfOBSzcdQKbh";

    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);
            String responseBody = response.getBody();
            System.out.println("Access Token Response: " + responseBody);

            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("access_token");
        } catch (HttpClientErrorException e) {
            // 에러 로그 추가
            System.err.println("Error during getAccessToken: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public KakaoDTO getUserInfo(String accessToken) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, request, String.class);
        String responseBody = response.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");

        String email = kakaoAccount.getString("email");
        String nickname = kakaoAccount.getJSONObject("profile").getString("nickname");

        return new KakaoDTO(jsonObject.getLong("id"), nickname, email);
    }
}
