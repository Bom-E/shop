import api from "../api";
import { loginSuccess, logout, setTokens } from "../redux/reducers/authSlice";
import { clearAuthCookies, getCookie } from "./cookies";

export const handleDefaultLogin = async (loginData, dispatch, navigate) => {

    try {
        const response = await api.post('/auth/login/defaultLogin', loginData);

        console.log('######:', loginData);

        if(response.data.status === 'success'){

            const accessToken = getCookie('access_token');
            const refreshToken = getCookie('refresh_token');

            if(accessToken && refreshToken){
                dispatch(setTokens({ accessToken, refreshToken }));
                dispatch(loginSuccess(response.data.user));
                
                // 로그인 성공
                alert(`${response.data.userId}님 환영합니다.`);
                navigate('/');
                return true;
            }
            console.error('Token not found.');
            return false;
        }
        return false;
    } catch (error) {
        dispatch(loginFailure(error.response?.data?.message));
        switch(error.response?.status){
            case 401:
                alert('아이디 또는 비밀번호가 일치하지 않습니다.');
                break;
            case 500 :
                alert('서버 오류가 발생했습니다.');
                break;
            default:
                alert('로그인 중 오류가 발생했습니다.');
        }
    }
};

const handleOAuth2Login = (provider) => {  
    // 인증 페이지로 리다이렉션
    window.location.href = `http://localhost:8081/oauth2/authorization/${provider}`;

};


// 이거 오어서2 콜백이랑 합쳐도 될 것 같은데?
useEffect(() => {
    const params = new URLSearchParams(window.location.search);
       
    if(params.toString()){

            console.log("Full URL: ", window.location.href);
            console.log("All params: ", Object.fromEntries(params));

            const status = params.get('status');
            const userData = {
                userId: params.get('userId')
                , email: params.get('email')
                , registrationId: params.get('registrationId')
                , userRole: params.get('userRole')
            };

        if(status === 'success'){

            console.log('OAuth2 Login - Status:', status);
            console.log('OAuth2 Login - UserData:', userData);

            if(userData.userId || userData.email){

                dispatch(loginSuccess(userData));
                dispatch(setTokens({
                    accessToken: getCookie('access_token')
                    , refreshToken: getCookie('refresh_token')
                }));
            }

            if(userData.userId){
                alert(`${userData.userId}님 환영합니다.`);
            } else if (userData.email){
                alert(`${userData.email}님 환영합니다.`);
            }
            navigate('/');
        }
    }
}, [dispatch, navigate]);

export const handleLoginError = (error) => {
    switch(error.response?.status){
        case 400:
            alert('잘못된 요청입니다.');
        case 401:
            alert('아이디 또는 비밀번호가 일치하지 않습니다.');
            break;
        case 500 :
            alert('서버 오류가 발생했습니다.');
            break;
        default:
            alert('로그인 중 오류가 발생했습니다.');
    }
}

export const handleLogout = (dispatch, navigate) => {
    return async () => {
        try{
            const response = await api.post('/auth/logout', null, { params: { logoutType: 'default' } });
            
            dispatch(setTokens({ accessToken: null, refreshToken: null }));
            dispatch(logout());
            clearAuthCookies();

            if(response.data.status === 'success'){
                alert(response.data.message);
            }
            
            navigate('/');
        } catch (error) {
            console.error("Logout failed:", error);
            alert("로그아웃 중 오류가 발생했습니다.");
        }
    }
};

export const checkTokenExpiration = async (dispatch, navigate) => {
    try{
        const response = await api.get('/auth/checkToken');
        switch(response.data.message){
            case "Valid access token":
                // 유효한 토큰 처리
                return true;
            case "Access token refreshed successfully":
                // 토큰 갱신 성공
                dispatch(setTokens({
                    accessToken: getCookie('access_token')
                    , refreshToken: getCookie('refresh_token')
                }));
                return true;
            default:
                throw new Error('Invalid token status');
        }
        
    } catch (error) {
        await handleLogout(dispatch, navigate)();

        if(error.response?.status === 401){
            alert("토큰이 만료 되었습니다. 다시 로그인해주세요.");
        } else {
            alert("오류가 발생했습니다. 다시 로그인해주세요.");
        }
        
        navigate('/auth/login');
        return false;
    }
};