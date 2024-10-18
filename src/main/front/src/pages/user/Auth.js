import React, { useState } from "react";
import springLogo from '../../assets/with_Spring_removebg.png';
import Sign1 from "./sign/Sign1";
import Sign2 from "./sign/Sign2";
import { useNavigate } from "react-router-dom";

const Auth = ({ className = '' }) => {
    const navigate = useNavigate();
    const [signupType, setsignupType] = useState('domestic');

    return(
        <div>
            <div className="flex flex-col items-center min-h-screen">
                <div className="flex justify-center items-center w-full mt-8 mb-4">
                    <img src={springLogo} alt="Spring Logo" onClick={() => navigate("/")} className="w-40 h-40 items-center object-contain"/>
                </div>
                <div className="flex space-x-4 mb-8">
                    <button 
                        className={`rounded-md border px-6 py-2 ${
                            signupType === 'domestic'
                            ? 'border-blue-500 text-blue-500'
                            : 'border-gray-300 text-gray-500 hover:border-blue-300 hover:text-blue-300'
                        }`}
                        onClick={() => setsignupType('domestic')}>
                        국내 회원
                    </button>
                    <button 
                        className={`rounded-md border px-6 py-2 ${
                            signupType === 'foreign'
                            ? 'border-blue-500 text-blue-500'
                            : 'border-gray-300 text-gray-500 hover:border-blue-300 hover:text-blue-300'
                        }`}
                        onClick={() => setsignupType('foreign')}>
                        해외 회원
                    </button>
                </div>
                <div className={`w-full h-auto px-4 ${className}`}>
                    {signupType === 'domestic' ? <Sign1 /> : <Sign2 />}
                </div>
            </div>
        </div>
    );
}

export default Auth;