package com.bom.shop.utility;

import com.bom.shop.user.vo.UserAccountVO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationUtil {
// 인증이 되고 난 후 이용자 정보 열람 시
// 클라이언트에서 넘어와서 서버에서 푼 데이터를 불러와 사용 할 때
// 도움이 된다.

    public Authentication createAuthentication(UserAccountVO userAccountVO){
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userAccountVO.getUserRole());

        Map<String, Object> principal = createPrincipal(userAccountVO);

        return new UsernamePasswordAuthenticationToken(
                principal
                , null
                , Collections.singletonList(authority)
        );
    }

    private Map<String, Object> createPrincipal(UserAccountVO userAccountVO){
        Map<String, Object> principal = new HashMap<>();
        principal.put("userId", userAccountVO.getUserId());

        if(userAccountVO.getRegistrationId() != null){
            principal.put("email", userAccountVO.getUserProfileVO().getEmail());
        }

        return principal;
    }

    public String getUserId(Authentication authentication){
        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        return (String) principal.get("userId");
    }

    public String getUserRole(Authentication authentication){
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .orElse(null);
    }

    public String getEmail(Authentication authentication){
        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        return (String) principal.get("email");
    }
}
