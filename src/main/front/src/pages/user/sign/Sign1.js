import axios from "axios";
import React, { useEffect, useCallback } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { loginFailure, loginSuccess, setRegistrationData, setTokens, setUser } from "../../../redux/reducers/authSlice";

const Sign1 = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const socialLogins = [
        { name: 'naver', color: 'bg-green-500', text: '네이버', icon: 'N' }
        , { name: 'google', color: 'bg-red-500', text: '구글', icon: 'G' }
        , { name: 'kakao', color: 'bg-yellow-500', text: '카카오', icon: 'K' }
    ];

    const handleAuthCallback = useCallback(async () => {
        try {
            const response = await axios.get('/api/oauth2/callback' + window.location.search);
            const data = response.data;

            if(data.accessToken){
                dispatch(setTokens({
                    accessToken: data.accessToken
                    , refreshToken: data.refreshToken
                }));
            }
            dispatch(setUser({
                email: data.email
                , registrationId: data.registrationId
                , isNewUser: data.isNewUser === 'true'
            }));

            if(data.isNewUser === 'true'){
                dispatch(setRegistrationData(data));
                navigate(data.redirectUrl);
            } else {
                alert('이미 회원가입 한 이메일 입니다.');
                dispatch(loginSuccess(data));
                navigate(data.redirectUrl);
            }

        } catch (error) {
            console.log('인증 처리 중 오류 발생:', error);
            dispatch(loginFailure(error.message));
        }
    }, [dispatch, navigate]);

    useEffect(() => {
        console.log("ok")

        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const error = urlParams.get('error');

        if(code){
           handleAuthCallback();
        }else if (error) {
            console.error('OAuth 인증 에러:', error);
        }
    }, [handleAuthCallback]);

    const handleSocialLogin = (socialName) => {
       
        // 인증 페이지로 리다이렉션
        window.location.href = `http://localhost:8081/oauth2/authorization/${socialName}`;

    };

    return(
        <div className="flex flex-col items-center justify-center bg-gray-100 p-4 w-30 h-[400px]">
            <div className="mb-4">
                <button  className="rounded-md border-2 border-sky-600 bg-transparent px-10 py-5 w-60 hover:bg-blue-600 hover:text-white transition duration-300 ease-in-out">
                    회원가입
                </button>
            </div>
            <div className="flex flex-col space-y-4 sm:flex-row sm:space-x-4 sm:space-y-0">
               {socialLogins.map(social => (
                <button 
                    key={social.name}
                    onClick={() => handleSocialLogin(social.name)} 
                    className={`${social.color} flex-shrink-0 rounded-full overflow-hidden w-24 h-24`}
                    >
                    <span className="text-2xl text-white">{social.icon}</span>
                </button>
               ))}
            </div>
        </div>
        
    )
};

export default Sign1;