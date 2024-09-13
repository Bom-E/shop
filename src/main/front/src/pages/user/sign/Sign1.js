import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useNavigate, useLocation } from "react-router-dom";

const API_BASE_URL = 'http://localhost:8081/user/sign1';

const Sign1 = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const socialLogins = [
        { name: 'naver', color: 'bg-green-500', text: '네이버', icon: 'N' }
        , { name: 'google', color: 'bg-red-500', text: '구글', icon: 'G' }
        , { name: 'kakao', color: 'bg-yellow-500', text: '카카오', icon: 'K' }
    ];

    useEffect(() => {

        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');

        if(token){
            localStorage.setItem('token', token);
            window.location.href = '/';
        }
    }, []);

    const handleSocialLogin = async (socialName) => {
        setLoading(true);
        setError(null);

        try {

            const { data } = await axios.post(`${API_BASE_URL}/oauth2/authorization/${socialName}`);
            const email = data.email;

            const response = await axios.post(`${API_BASE_URL}`, { email });

            if(response.status === 200){
                // 이미 회원임
                navigate("/");
            }

        } catch (error) {

            if(error.response && error.response.status === 404){
                // 회원이 없음
                const { email, registrationId } = error.response.data;

                navigate("/domSignup", { state : { email, registrationId } });
            } else {
                console.error("Error:", error);
            }

        }

    };

    return(
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
            <div className="mb-8">
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
                    disabled={loading}>
                    <span className="text-2xl text-white">{social.icon}</span>
                    <span>{loading ? '로그인 중...' : `${social.text}로 회원가입`}</span>
                </button>
               ))}
            </div>
            {error && <p className="mt-4 text-red-500">{error}</p>}
        </div>
        
    )
};

export default Sign1;