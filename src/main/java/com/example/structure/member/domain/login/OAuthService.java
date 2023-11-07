package com.example.structure.member.domain.login;

import com.example.structure.member.domain.Member;
import com.example.structure.member.domain.login.model.GetSocialOAuthRes;
import com.example.structure.member.domain.login.model.GoogleOAuthToken;
import com.example.structure.member.domain.login.model.GoogleUser;
import com.example.structure.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;
private final AccountProvider accountProvider;
private  final JwtService jwtService;
private final MemberRepository memberRepository;
    public String request(Constant.SocialLoginType socialLoginType) throws IOException {
        String redirectURL;
        switch (socialLoginType) {
            case GOOGLE: {
                redirectURL = googleOauth.getOauthRedirectURL();
                return redirectURL;
            }
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }
    }
    public String getRedirectURL(Constant.SocialLoginType socialLoginType) {
        switch (socialLoginType) {
            case GOOGLE:
                return googleOauth.getOauthRedirectURL();
            default:
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }
    }
    public GetSocialOAuthRes oAuthLogin(Constant.SocialLoginType socialLoginType, String code) throws IOException {

        switch (socialLoginType){
            case GOOGLE:{
                //구글로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
                ResponseEntity<String> accessTokenResponse= googleOauth.requestAccessToken(code);
                //응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담을 것이다.
                GoogleOAuthToken oAuthToken=googleOauth.getAccessToken(accessTokenResponse);

                //액세스 토큰을 다시 구글로 보내 구글에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse=googleOauth.requestUserInfo(oAuthToken);
                //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                GoogleUser googleUser= googleOauth.getUserInfo(userInfoResponse);

                String user_id = googleUser.getEmail();
                int user_num=accountProvider.getUserNum(user_id);

                if(user_num!=0){
                    //서버에 user가 존재하면 앞으로 회원 인가 처리를 위한 jwtToken을 발급한다.
                    String jwtToken=jwtService.createJwt(user_num,user_id);
                    //액세스 토큰과 jwtToken, 이외 정보들이 담긴 자바 객체를 다시 전송한다.
                    GetSocialOAuthRes getSocialOAuthRes=new GetSocialOAuthRes(jwtToken,user_num,oAuthToken.getAccess_token(),oAuthToken.getToken_type());
                    System.out.println(getSocialOAuthRes);
                    return getSocialOAuthRes;
                }
                else {
                    Member newMember = new Member();
                    newMember.setMemberSocialid(user_id); // 구글에서 가져온 사용자 아이디를 설정합니다
                    newMember.setMemberName(googleUser.getName()); // 구글에서 가져온 사용자 이름을 설정합니다
                    // 나머지 필드들도 적절히 설정해주세요.

                    Member savedMember = memberRepository.save(newMember); // 회원 정보를 저장합니다
                }

            }
            default:{
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }

    }
}
