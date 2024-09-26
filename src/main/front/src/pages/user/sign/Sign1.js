import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Sign1 = () => {
    const [loading] = useState(false);
    const navigate = useNavigate();

    const socialLogins = [
        { name: 'naver', color: 'bg-green-500', text: '네이버', icon: 'N' }
        , { name: 'google', color: 'bg-red-500', text: '구글', icon: 'G' }
        , { name: 'kakao', color: 'bg-yellow-500', text: '카카오', icon: 'K' }
    ];

    useEffect(() => {
        console.log("ok")
        const urlParams = new URLSearchParams(window.location.search);
        const email = urlParams.get('email');
        const registrationId = urlParams.get('registrationId');

        console.log("Email:", email, "RegistrationId:", registrationId);

        if(email && registrationId){
           navigate("/domSignup", { state: {email, registrationId} })
        }
    }, [navigate]);

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
                    disabled={loading}>
                    <span className="text-2xl text-white">{social.icon}</span>
                </button>
               ))}
            </div>
        </div>
        
    )
};

export default Sign1;