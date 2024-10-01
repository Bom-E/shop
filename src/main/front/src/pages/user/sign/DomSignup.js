import { useEffect, useState } from "react";
import { useDispatch, useSelector, useStore } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { setTokens, setUser } from "../../../redux/reducers/authSlice";
import axios from "axios";

const DomSignup = () => {
    const location = useLocation();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: ''
        , userPw: ''
        , passwordConfirm: ''
        , userName:''
        , userTel: ''
        , birthDate: ''
        , gender: ''
        , userId: ''
        , registrationId: ''
    });
    const [ isSSO, setIsSSO ] = useState(false); 

    const user = useSelector(state => state.auth.user);
    const isNewUser = user ? user.isNewUser : null;

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const email = params.get('email');
        const registrationId = params.get('registrationId');
        const isNewUser = params.get('isNewUser') === 'true';

        if(email && registrationId){
            setFormData(t => ({
                ...t
                , email
                , registrationId
            }));
            setIsSSO(true);
            dispatch(setUser({ email, registrationId, isNewUser }));
        }
    
    }, [location, dispatch]);

    useEffect(() => {
        if(isNewUser === false){
            navigate('/')
        }
    }, [isNewUser, navigate]);

    const handleEmailhidden = (e) => {
        const { name, value } = e.target;
        setFormData(t => ({ ...t, [name]: value }));
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/userSign/sign1/domSignup', formData, { withCredentials: true });
            if(response.data.message === "Signup successful"){
                alert('가입되었 습니다.');
                navigate('/');
            }
        } catch (error) {
            console.log('Signup failed:', error);
        }
    }

    return(
        <form onSubmit={handleSubmit} className="flex flex-col items-center">
            <div>{isSSO ? 'SNS 간편가입' : '일반 회원가입'}</div>
            <hr className="w-full my-4"></hr>
            <div>
                <span>아이디 </span>
                <span>*</span>
            </div>
            <div>
                <input name="userId" value={formData.userId} onChange={handleEmailhidden} readOnly={isSSO} placeholder="아이디를 입력해 주세요" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>

            {!isSSO && (
                <>
                    <div>
                        <span>비밀번호 </span>
                        <span>*</span>
                    </div>
                    <div>
                        <input name="userPw"value={formData.userPw} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="비밀번호를 입력해 주세요."/>
                    </div>
                    <div>
                        <span>비밀번호 확인 </span>
                        <span>*</span>
                    </div>
                    <div>
                        <input className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="비밀번호를 한 번 더 입력해 주세요."/>
                    </div>
                </>
            )}

            <div>
                <span>이름 </span>
                <span>*</span>
            </div>
            <div>
                <input name="userName" value={formData.userName} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="이름을 입력해 주세요."/>
            </div>
            <div>
                <span>휴대번호 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input name="userTel" value={formData.userTel} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="숫자만 입력해 주세요."/></span>
                <button>인증번호 발송</button>
            </div>
            <div>
                <span>이메일 </span>
                <span>*</span>
            </div>
            <div>
                <span><input name="email" value={formData.email} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly placeholder={`${formData.email  != null ? `${formData.email}` : `이메일을 입력해 주세요.`} `}/></span>
            </div>
            <div>
                <span>생년월일/성별 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input name="birthDate" value={formData.birthDate} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="생년월일 8자리를 입려해 주세요."/></span>
                <button type="submit" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                    회원가입 완료
                </button>
            </div>
        </form>
    )
};

export default DomSignup;