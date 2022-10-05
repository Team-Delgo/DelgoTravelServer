package com.delgo;

import com.delgo.api.comm.oauth.KakaoService;
import com.delgo.api.comm.oauth.NaverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OAuthTest {

    @Autowired
    private KakaoService kakaoService;

    @Autowired
    private NaverService naverService;

    @Test
    public void KakaoLogin() throws Exception {
        String code = "wpNARijg_Z65hnQ6DwBxGrOip08idRmnd1a73XrNWVt1qpKnYc_1YvWAZ0BPc9Xls933ogo9dNsAAAGDhvP6HA";
        String token = kakaoService.getKakaoAccessToken(code);
        kakaoService.createKakaoUser(token);
    }

    @Test
    public void NaverLogin() throws Exception {
//       https://www.delgo.pet/oauth/callback/naver?code=GWx84ipdno3k59z44N&state=9kgsGTfH4j7IyAkg
        String state = "9kgsGTfH4j7IyAkg";
        String code = "GWx84ipdno3k59z44N";

        String token = naverService.getNaverAccessToken(state, code);

        naverService.createNaverUser(token);
    }

    @Test
    public void NaverLogOut() throws Exception {
//       https://www.delgo.pet/oauth/callback/naver?code=GWx84ipdno3k59z44N&state=9kgsGTfH4j7IyAkg
        String accessToken = "AAAAOvSSVipN25hL4qLU13pIpDsZbgchc6H27vkyDvzLwNLyBMhzzMobL3wcWS0Mqp77VYCtE20jeCVkoo4CpgtdY70";
        naverService.deleteNaverUser(accessToken);

    }
}
