import { useNavigate } from 'react-router-dom';
import React, { useEffect, useRef, useState } from "react";
import springLogo from '../../../assets/with_Spring_removebg.png'
import { Eye, EyeOff } from 'lucide-react';
import api from '../../../api';
import { useDispatch, useSelector } from 'react-redux';
import { loginFailure, loginSuccess, selectIsLoggedIn, selectToken, setTokens } from '../../../redux/reducers/authSlice';
import { getCookie } from '../../../utils/cookies';
import store from '../../../redux/store';

const LoginPage = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const isLoggedIn = useSelector(selectIsLoggedIn);
    const tokens = useSelector(selectToken);
    const [showPassword, setShowPassword] = useState(false);
    const passwordRef = useRef(null);
    const [loginData, setLoginData] = useState({
        userId: ''
        , userPw: ''
    });

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
        if(passwordRef.current){
            passwordRef.current.type = showPassword ? 'password' : 'text';
        }
    };

    const socialLogins = [
        { name: 'naver', color: 'bg-green-500', text: '네이버', icon: 'N' }
        , { name: 'google', color: 'bg-red-500', text: '구글', icon: 'G' }
        , { name: 'kakao', color: 'bg-yellow-500', text: '카카오', icon: 'K' }
    ];

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setLoginData(prev=> ({
            ...prev
            , [name] : value
        }));
    };

    useEffect(() => {
        if(tokens.accessToken){
            
            const checkToken = async () => {
                try{
                    
                    const response = await api.get('/auth/checkToken');

                    if(response.data.message === "Valid access token"){
                        console.log("유효한 토큰");
                    } else if(response.data.message === "Access token refreshed successfully"){
                        console.log("토큰 갱신 완료");
                        dispatch(setTokens({
                            accessToken: getCookie('access_token')
                            , refreshToken: getCookie('refresh_token')
                        }));
                    } 

                } catch (error) {
                    console.log("Token check failed:", error)
                    alert("비정상적 토큰 감지, 다시 로그인 해주세요.");
                    navigate('/auth/login');
                }
            };

            checkToken();
        }
    }, []);

    const handleSocialLogin = (socialName) => {
       
        // 인증 페이지로 리다이렉션
        window.location.href = `http://localhost:8081/oauth2/authorization/${socialName}`;
    
    };

    useEffect(() => {
        // 페이지 로드 시 한 번만 실행 되도록 빈 의존 배열 사용
    }, []);

    return (
        <div className='flex flex-col items-center min-h-screen'>
            <form onSubmit={handleDefaultLogin}>
                <div className='flex justify-center items-center w-full mt-4 sm:mt-6 md:mt-8 mb-4'>
                    <img src={springLogo} alt="Spring Logo" onClick={() => navigate("/")} className="w-32 h-32 sm:w-36 sm:h-36 md:w-40 md:h-40 items-center object-contain cursor-pointer"/>
                </div>
                <div className="w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg px-4 sm:px-0">
                    <div className='mb-2'>
                        <input 
                            name='userId' 
                            value={loginData.userId}
                            onChange={handleInputChange}
                            className='w-full p-2 sm:p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-black' 
                            placeholder='아이디를 입력해주세요.'/>      
                    </div>
                    <div className='mb-2 relative'>
                        <input
                            name='userPw'
                            value={loginData.userPw}
                            type='password'
                            onChange={handleInputChange}
                            ref={passwordRef} 
                            className='w-full p-2 sm:p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-black' 
                            placeholder='비밀번호를 입력해주세요.'
                            autoComplete="new-password"/>
                        <button 
                            type='button' 
                            onClick={togglePasswordVisibility} 
                            className='absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 focus:ouline-none'>
                            {showPassword ? <Eye size={18} className='sm:w-5 sm:h-5'/> : <EyeOff size={18} className='sm:w-5 sm:h-5'/>}
                        </button>
                    </div>
                    <button
                        type='submit' 
                        className='w-full p-2 sm:p-3 rounded-md text-gray-600 bg-yellow-100 hover:bg-yellow-200 transition-colors'>
                        로그인
                    </button>
                    <div className='mt-2 mb-6 sm:mb-8 md:mb-10 flex justify-end space-x-1'>
                        <button className='text-gray-600 text-xs sm:text-sm hover:text-gray-800'>
                            아이디 찾기
                        </button>
                        <span className='text-gray-600 text-xs sm:text-sm'>|</span>
                        <button className='text-gray-600 text-xs sm:text-sm hover:text-gray-800'>
                            비밀번호 찾기
                        </button>
                    </div>
                </div>
            </form>
            <div className="flex flex-col space-y-3 sm:flex-row sm:space-x-4 sm:space-y-0">
               {socialLogins.map(social => (
                <button 
                    key={social.name}
                    onClick={() => handleSocialLogin(social.name)} 
                    className={`${social.color} flex-shrink-0 rounded-full overflow-hidden w-16 h-16 sm:w-20 sm:h-20 md:w-24 md:h-24 flex items-center justify-center transition-transform hover:scale-105`}
                    >
                    <span className="text-xl sm:text-2xl text-white">{social.icon}</span>
                </button>
               ))}
            </div>
        </div>
    )
}

export default LoginPage;