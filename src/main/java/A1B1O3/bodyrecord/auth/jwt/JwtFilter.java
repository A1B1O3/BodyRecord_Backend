package A1B1O3.bodyrecord.auth.jwt;

import A1B1O3.bodyrecord.auth.domain.PrincipalDetails;
import A1B1O3.bodyrecord.auth.service.PrincipalOAuth2DetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;


    /* 인증 과정을 처리하는 커스텀 필터
     * JWT 토큰의 인증 정보를 현재 스레드의 SecurityContext에 저장하는 역할을 수행 */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("[JwtFilter] : doFilterInternal start =====================================");

        // 1. request에서 token을 꺼낸다
        String token = resolveToken(request);

        // 2. validateToken으로 토큰 유효성을 검사한다
        // 정상 토큰일 경우 해당 토큰으로 Authentication을 가져와서 SecurityContext에 저장한다
        // 그래야 이후 로직을 통해 인가에 대한 부분이 처리 될 수 있다

        try {

            if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                Long memberCode = Long.parseLong(jwtTokenProvider.getUseridFromAcs(token));
                PrincipalDetails userDetails = (PrincipalDetails) principalOAuth2DetailsService.loadUser(Math.toIntExact(memberCode));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("[JwtFilter] : 잘못 된 JWT 서명입니다.");
        }  catch (UnsupportedJwtException e) {
            log.info("[JwtFilter] : 지원 되지 않는 JWT 서명입니다.");
        } catch (IllegalArgumentException e) {
            log.info("[JwtFilter] : JWT 토큰이 잘못 되었습니다.");
        } catch (ExpiredJwtException e) {
            log.info("[JwtFilter] : 만료 된 JWT 서명입니다.");
        }

        // 3. 다음 필터로 진행한다
        filterChain.doFilter(request, response);

        log.info("[JwtFilter] : doFilterInternal end =====================================");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }



}
