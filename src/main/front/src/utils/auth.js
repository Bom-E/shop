import api from "../api";
import { setTokens } from "../redux/reducers/authSlice";
import { getCookie } from "./cookies";

export const handleLogout = (dispatch, navigate) => {
    return async () => {
        try{
            await api.post('/auth/logout');
            dispatch(setTokens({ accessToken: null, refreshToken: null }));
            navigate('/');
        } catch (error) {
            console.error("Logout failed:", error);
        }
    }
};

export const checkTokenExpiration = async (dispatch, navigate) => {
    try{
        const response = await api.get('/auth/checkToken');
        if(response.data.message === "Access token refreshed successfully"){
            dispatch(setTokens({
                accessToken: getCookie('access_token')
                , refreshToken: getCookie('refresh_token')
            }));
        }
    } catch (error) {
        alert("토큰이 만료 되었습니다. 다시 로그인 해주세요.");
        navigate('/auth/login');
    }
};