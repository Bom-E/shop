import React, {useState} from "react";
import axios from 'axios';

const API_BASE_URL = 'http://localhost:3000/userSign/sign1';

const Sign1 = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const socialLogins = [
        { name: 'naver', color: 'bg-green-500', text: '네이버', icon: 'N' }
        , { name: 'google', color: 'bg-red-500', text: '구글', icon: 'G' }
        , { name: 'kakao', color: 'bg-yellow-500', text: '카카오', icon: 'K' }
    ];

    const handleSocialLogin = async loginType => {
        setLoading(true);
        setError(null);

        try {
            const response = await axios.post(`${API_BASE_URL}/social-login`, { loginType });
            console.log('로그인 요청 결과:', response.data);
        } catch (error) {
            console.error('로그인 요청 에러:', error);
            setError('로그인 중 오류가 발생했습니다. 다시 시도해 주세요.');
        } finally {
            setLoading(false);
        }

    }

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
                    disable={loading}>
                    <span className="text-2xl text-white">{social.icon}</span>
                    <span>{loading ? '로그인 중...' : `${social.text}로 로그인`}</span>
                </button>
               ))}
            </div>
            {error && <p className="mt-4 text-red-500">{error}</p>}
        </div>
        
    )
};

export default Sign1;