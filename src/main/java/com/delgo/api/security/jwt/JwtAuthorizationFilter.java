package com.delgo.api.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.delgo.api.domain.user.User;
import com.delgo.api.repository.UserRepository;
import com.delgo.api.security.services.PrincipalDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 인가
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // Token Check
    @Override
    @Transactional
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(Access_JwtProperties.HEADER_STRING);

        if (request.getRequestURI().equals("/signup") || request.getRequestURI().equals("/emailCheck")
                || request.getRequestURI().equals("/phoneNoCheck") || request.getRequestURI().equals("/authRandNum")
                || request.getRequestURI().equals("/authRandNum") || request.getRequestURI().equals("/place/selectAll")
                || request.getRequestURI().equals("/emailAuth") || request.getRequestURI().equals("/changePassword")
                || request.getRequestURI().equals("/tokenReissue")) {
            chain.doFilter(request, response);
            return;
        }

        // Token 있는지 여부 체크
        if (header == null || !header.startsWith(Access_JwtProperties.TOKEN_PREFIX)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/tokenError");
            dispatcher.forward(request, response);
            return;
        }

        String token = request.getHeader(Access_JwtProperties.HEADER_STRING)
                .replace(Access_JwtProperties.TOKEN_PREFIX, "");

        // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
        // SecurityContext에 접근해서 세션을 만들때 자동으로 UserDetailsService에 있는 loadByUsername이 호출됨.
        try {
            String email = JWT.require(Algorithm.HMAC512(Access_JwtProperties.SECRET)).build().verify(token)
                    .getClaim("email").asString();

            System.out.println("JwtAuthorizationFilter email : " + email);
            if (email != null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new NullPointerException("Not Found UserData"));

                System.out.println("JwtAuthorizationFilter findByEmail : " + user.toString());

                // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
                // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
                PrincipalDetails principalDetails = new PrincipalDetails(user);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                                null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                                principalDetails.getAuthorities());

                // 강제로 시큐리티의 세션에 접근하여 값 저장 ( 권한 없으면 필요 없음 )
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) { // Token 시간 만료 및 토큰 인증 에러
            System.out.print("Access Token Expired : " + e.getLocalizedMessage());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/tokenError");
            dispatcher.forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
