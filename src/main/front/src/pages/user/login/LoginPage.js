import { useNavigate } from 'react-router-dom';
import React, { useRef, useState } from "react";
import springLogo from '../../../assets/with_Spring_removebg.png'
import { Eye, EyeOff } from 'lucide-react';

const LoginPage = () => {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const passwordRef = useRef(null);

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

    const handleSocialLogin = (socialName) => {
       
        // 인증 페이지로 리다이렉션
        window.location.href = `http://localhost:8081/oauth2/authorization/${socialName}`;

    };

    return (
        <div className='flex flex-col items-center min-h-screen'>
            <div className='flex justify-center items-center w-full mt-8 mb-4'>
                <img src={springLogo} alt="Spring Logo" onClick={() => navigate("/")} className="w-40 h-40 items-center object-contain"/>
            </div>
            <div className="w-2/5 max-w-sm">
                <div className='mb-0.5'>
                    <input 
                        name='userId' 
                        className='w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-black' 
                        placeholder='아이디를 입력해주세요.'/>      
                </div>
                <div className='mb-0.5 relative'>
                    <input
                        name='userPw'
                        type='password'
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
                <button className='w-full p-3 rounded-md text-gray-600 bg-yellow-100 hover:bg-yellow-200'>
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