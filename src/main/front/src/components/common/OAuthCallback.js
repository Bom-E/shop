import { useNavigate, useParams } from "react-router-dom";
import api from "../../api";
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { getCookie } from "../../utils/cookies";
import { loginSuccess, setTokens } from "../../redux/reducers/authSlice";

export const OAuthCallback = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { provider } = useParams();

    useEffect(() => {
        
        const params = new URLSearchParams(window.location.search);
        console.log("OAuth callback - Full URL:", window.location.href);
        console.log("OAuth Callback - All params:", Object.fromEntries(params));
        console.log("OAuth provider:", provider);

        const code = params.get('code');
        const error = params.get('error');

        if(error){
            console.error("OAuth error:", error);
            alert("로그인 중 오류가 발생했습니다.");
            navigate('/auth/login');
            return ;
        }

        if(code){
            const fetchUserInfo = async () => {
                try{
                    const response = await api.post(`/auth/oauth2/callback/${provider}`, {
                        code
                        , state: params.get('state')
                    });

                    if(response.datat.status === 'success'){
                        const userData = {
                            userId: response.data.userId
                            , email: response.data.email
                            , registrationId: provider
                            , userRole: response.data.userRole
                        };

                        dispatch(loginSuccess(userData));
                        dispatch(setTokens({
                            accessToken: getCookie('access_token')
                            , refreshToken: getCookie('refresh_token')
                        }));

                        if(userData.userId){
                            alert(`${userData.userId}님 환영합니다!`);
                        } else if(userData.email){
                            alert(`${userData.email}님 환영합니다!`);
                        }
                        navigate('/');
                    } else {
                        throw new Error("Login failed");
                    }
                } catch (error) {
                    console.error("OAuth login failed:", error);
                    alert("로그인 처리 중 오류가 발생했습니다.");
                    navigate('/auth/login');
                }
            };

            fetchUserInfo();
        } else {
            console.error("No authentication code received");
            navigate('/auth/login');
        }

    }, [dispatch, navigate, provider]);
};

export default OAuthCallback;