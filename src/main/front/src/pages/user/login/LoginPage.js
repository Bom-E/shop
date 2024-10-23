import { useNavigate } from 'react-router-dom';
import React, { useRef, useState } from "react";
import springLogo from '../../../assets/with_Spring_removebg.png'
import { Eye, EyeOff } from 'lucide-react';

const LoginPage = () => {
    const navigate = useNavigate();
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
            const response = await api.post('http://localhost:8081/auth/login', loginData);

            if(response.data.status === 'success'){
                // 로그인 성공
                navigate('/');
            }
        } catch (error) {
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

    const handleSocialLogin = (socialName) => {
       
        // 인증 페이지로 리다이렉션
        window.location.href = `http://localhost:8081/oauth2/authorization/${socialName}`;

    };

    return (
        <div className='flex flex-col items-center min-h-screen'>
            <form onSubmit={handleDefaultLogin}>
                <div className='flex justify-center items-center w-full mt-8 mb-4'>
                    <img src={springLogo} alt="Spring Logo" onClick={() => navigate("/")} className="w-40 h-40 items-center object-contain"/>
                </div>
                <div className="w-2/5 max-w-sm">
                    <div className='mb-0.5'>
                        <input 
                            name='userId' 
                            value={loginData.userId}
                            onChange={handleInputChange}
                            className='w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-black' 
                            placeholder='아이디를 입력해주세요.'/>      
                    </div>
                    <div className='mb-0.5 relative'>
                        <input
                            name='userPw'
                            value={loginData.userPw}
                            type='password'
                            onChange={handleInputChange}
                            ref={passwordRef} 
                            className='w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-black' 
                            placeholder='비밀번호를 입력해주세요.'
                            autoComplete="new-password"/>
                        <button 
                            type='button' 
                            onClick={togglePasswordVisibility} 
                            className='absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 focus:ouline-none'>
                            {showPassword ? <Eye size={20}/> : <EyeOff size={20}/>}
                        </button>
                    </div>
                    <button
                        type='submit' 
                        className='w-full p-3 rounded-md text-gray-600 bg-yellow-100 hover:bg-yellow-200'>
                        로그인
                    </button>
                    <div className='mb-10 flex justify-end '>
                        <button className='text-gray-600 text-xs'>
                            아이디 찾기 | 
                        </button>
                        <button className='text-gray-600 text-xs'>
                            비밀번호 찾기
                        </button>
                    </div>
                </div>
            </form>
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
}

export default LoginPage;