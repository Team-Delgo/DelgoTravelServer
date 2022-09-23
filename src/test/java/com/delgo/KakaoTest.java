package com.delgo;

import com.delgo.api.comm.OAuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KakaoTest {

    @Autowired
    private OAuthService oAuthService;

    @Test
    public void KakaoLogin() throws Exception {
        String code = "UJheCZXCNTdrgLtWTxOF0aDgNn2z3DEMOM6DN2IKSwy1fmy4id-eWv54Ucwt1lpw-TtPkwopyNoAAAGCdlAKDA";
        String token = oAuthService.getKakaoAccessToken(code);
        oAuthService.createKakaoUser(token);
    }
}
