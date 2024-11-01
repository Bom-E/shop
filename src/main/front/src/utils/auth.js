import api from "../api";
import { logout, setTokens } from "../redux/reducers/authSlice";
import { getCookie } from "./cookies";


export const handleLogout = (dispatch, navigate) => {
    return async () => {
        try{
            const response = await api.post('/auth/logout', null, { params: { logoutType: 'default' } });
            dispatch(setTokens({ accessToken: null, refreshToken: null }));
            dispatch(logout());

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