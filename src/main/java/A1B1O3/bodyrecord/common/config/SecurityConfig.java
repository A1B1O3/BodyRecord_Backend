package A1B1O3.bodyrecord.common.config;

import A1B1O3.bodyrecord.auth.OAuth2SuccessHandler;
import A1B1O3.bodyrecord.auth.jwt.JwtAccessDeniedHandler;
import A1B1O3.bodyrecord.auth.jwt.JwtAuthenticationEntryPoint;
import A1B1O3.bodyrecord.auth.jwt.JwtFilter;
import A1B1O3.bodyrecord.auth.jwt.JwtTokenProvider;
import A1B1O3.bodyrecord.auth.service.AuthService;
import A1B1O3.bodyrecord.auth.service.PrincipalOAuth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private static final String[] DOC_URLS = {
            "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html","/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http

                    // CSRF 설정 Disable
                    .csrf()
                    .disable()
                    // 세션 미사용
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    // 요청에 대한 권한 체크
                    .authorizeRequests()
                    .antMatchers("/oauth2/**").permitAll()
                    .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()

                .and()
                    // oauth2 login 처리 service 설정
                    .oauth2Login()
                    .userInfoEndpoint()
                    .userService(principalOAuth2DetailsService)
                .and()
                    // oauth2 성공 시
                    .successHandler(new OAuth2SuccessHandler(jwtTokenProvider, authService))
                .and()
                    // 인증에서 사용 되는 필터 설정
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                    // 인증 실패 시 처리
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    // 인가 실패 시 처리
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                    .cors()
                .and()
                .build();

    }

    /* CORS(cross-origin-resource-sharing) : 교차 출처 자원 공유
     * 외부 도메인과 통신하기 위한 표준
     * 서버에서 클라이언트를 대상으로 리소스의 허용 여부를 결정
     * */
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 로컬 React에서 오는 요청은 CORS 허용해준다.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000" ));
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Content-Type", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }






}
