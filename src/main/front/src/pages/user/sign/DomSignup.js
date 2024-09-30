import { useEffect, useState } from "react";
import { useDispatch, useSelector, useStore } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { setTokens, setUser } from "../../../redux/reducers/authSlice";

const DomSignup = () => {
    const location = useLocation();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [ isSSO, setIsSSO ] = useState(false); 

    const { isNewUser } = useSelector(state => state.auth.user);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const accessToken = params.get('accessToken');
        const refreshToken = params.get('refreshToken');
        const emailParam = params.get('email');
        const registrationId = params.get('registrationId');
        const isNewUserParam = params.get('isNewUser') === 'true';
    
        if(accessToken && refreshToken && emailParam){
            dispatch(setTokens({ accessToken, refreshToken }));
            dispatch(setUser({ email: emailParam, registrationId, isNewUser: isNewUserParam }));
            setEmail(emailParam);
            setIsSSO(true);
        } else {
            setIsSSO(false);
            
        }
    
    }, [location, dispatch]);

    useEffect(() => {
        if(isNewUser === false){
            navigate('/')
        }
    }, [isNewUser, navigate]);

    const handleEmailhidden = (e) => {
        if(!isSSO){
            setEmail(e.target.value);
        }
    }

    return(
        <div className="flex flex-col items-center">
            <div>{isSSO ? 'SNS 간편가입' : '일반 회원가입'}</div>
            <hr className="w-full my-4"></hr>
            <div>
                <span>아이디 </span>
                <span>*</span>
            </div>
            <div>
                <input id="email" type="email" value={email} onChange={handleEmailhidden} readOnly={isSSO} placeholder={!isSSO ? '아이디를 입력해 주세요' : ''} className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${isSSO ? 'bg-gray-100' : ''}`}/>
            </div>
            <div>
                <span>비밀번호 </span>
                <span>*</span>
            </div>
            <div>
                <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="placeholder:비밀번호를 입력해 주세요."/>
            </div>
            <div>
                <span>비밀번호 확인 </span>
                <span>*</span>
            </div>
            <div>
                <input placeholder="비밀번호를 한 번 더 입력해 주세요."/>
            </div>
            <div>
                <span>이름 </span>
                <span>*</span>
            </div>
            <div>
                <input className=""/>
            </div>
            <div>
                <span>휴대번호 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input className="placeholder:숫자만 입력해 주세요."/></span>
                <button>인증번호 발송</button>
            </div>
            <div>
                <span>이메일 </span>
                <span>*</span>
            </div>
            <div>
                <span><input readOnly className={`${email  != null ? `placeholder:${email}` : `placeholder:이메일을 입력해 주세요.`} `}/></span>
            </div>
            <div>
                <span>생년월일/성별 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input className="placeholder:생년월일 8자리를 입려해 주세요."/></span>
                <button></button>
            </div>
        </div>
    )
};

export default DomSignup;