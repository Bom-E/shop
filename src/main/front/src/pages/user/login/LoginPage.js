import { useNavigate } from 'react-router-dom';
import React, { useEffect, useRef, useState } from "react";
import springLogo from '../../../assets/with_Spring_removebg.png'
import { Eye, EyeOff } from 'lucide-react';
import api from '../../../api';
import { useDispatch, useSelector } from 'react-redux';
import { loginFailure, loginSuccess, selectIsLoggedIn, selectToken, setTokens } from '../../../redux/reducers/authSlice';

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

    const handleDefaultLogin = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post('/auth/login/defaultLogin', loginData);

            console.log('######:', loginData);

            if(response.data.status === 'success'){

                dispatch(loginSuccess(response.data.user));
                dispatch(setTokens({
                    accessToken: response.data.accessToken
                    , refreshToken: response.data.refreshToken
                }));
                // 로그인 성공
                alert(`${response.data.userId}님 환영합니다.`);
                navigate('/');
                
            }
        } catch (error) {
            dispatch(loginFailure(error.response?.data?.message));
            switch(error.response?.status){
                case 401:
                    alert('아이디 또는 비밀번호가 일치하지 않습니다.');
                case 500 :
                    alert('서버 오류가 발생했습니다.');
                    break;
                default:
                    alert('로그인 중 오류가 발생했습니다.');
            }
        }
    };

    useEffect(() => {
        if(tokens.accessToken){
            // ㅌ큰 만료 로직 만들거임
        }
    }, [tokens.accessToken]);

    useEffect(() => {

        const params =  new URLSearchParams(window.location.search);
        const status = params.get('status');
        const userData = {
            userId: params.get('userId')
            , email: params.get('email')
            , registrationId: params.get('registrationId')
            , userRole: params.get('userRole')
        };

        if(status === 'success'){
            dispatch(loginSuccess(userData));
            dispatch(setTokens({
                accessToken: params.get('accessToken')
                , refreshToken: params.get('refreshToken')
            }));
            alert(`${userData.userId}님 환영합니다.`);
            console.log('test');
            navigate('/');
        }

    }, [dispatch, navigate]);

    const handleSocialLogin = (socialName) => {
       
        // 인증 페이지로 리다이렉션
        window.location.href = `http://localhost:8081/oauth2/authorization/${socialName}`;

    };

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