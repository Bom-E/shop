import { useNavigate, useParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { getCookie } from "../../utils/cookies";
import { loginSuccess, setTokens } from "../../redux/reducers/authSlice";

export const OAuthCallback = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { provider } = useParams();

    useEffect(() => {

        console.log("Currnet cookies:", document.cookie);

        // 토큰 확인
        const accessToken = getCookie('access_token');
        const refreshToken = getCookie('refresh_token');
        console.log("Access Token:", accessToken);
        console.log("Refresh Token:", refreshToken);
        
        // URL 파라미터에서 사용자 정보 가져오기
        const params = new URLSearchParams(window.location.search);
        const status = params.get('status');
        console.log("OAuth callback - Full URL:", window.location.href);
        params.forEach((value, key) => {
            console.log(`${key}: ${value}`);
        })
        console.log("OAuth provider:", provider);
        console.log("Status:", status);

        if(status === 'success'){

            const userData = {
                userId: params.get('userId')
                , email: params.get('email')
                , registrationId: provider
                , userRole: params.get('userRole')
            };
            if(accessToken && refreshToken){
                dispatch(setTokens({
                    accessToken
                    , refreshToken
                }));
            }
    
            dispatch(loginSuccess(userData));

            if(userData.userId){
                alert(`${userData.userId}님 환영합니다!`);
            } else if(userData.email){
                alert(`${userData.email}님 환영합니다!`);
            }

            navigate('/', { replace: true });

        } else {
            console.error("Authentication failed");
            alert("다시 로그인해주세요.");
            navigate('/auth/login', { replace: true });
        }
    }, [dispatch, navigate, provider]);
};

export default OAuthCallback;