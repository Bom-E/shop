import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { setUser } from "../../../redux/reducers/authSlice";
import api from "../../../api/index";

const DomSignup = () => {
    const location = useLocation();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [formData, setFormData] = useState({
        email: ''
        , userPw: ''
        , userName:''
        , userTel: ''
        , birthDate: ''
        , gender: 'male'
        , userId: ''
        , registrationId: ''
    });
    const [ isSSO, setIsSSO ] = useState(false); 
    const [ pwConfirm, setPwConfirm ] = useState('');

    const user = useSelector(state => state.auth.user);
    const isNewUser = user ? user.isNewUser : null;

    const dataCheck = () => {
        if(!formData.userId || !formData.email || !formData.birthDate || !formData.gender || !formData.userName || !formData.userTel){
            setError("모든 필수 항목을 입력해주세요.");
            return false;
        }

        if(!isSSO){
            if(!formData.userPw || !pwConfirm){
                setError("비밀번호를 입력해주세요.");
                return false;
            }
            if(formData.userPw !== pwConfirm){
                setError("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }

        return true;
    }

    useEffect(() => {
        const isPath = location.pathname.includes('domSignup');

        if(isPath){
            const params = new URLSearchParams(location.search);
            const email = params.get('email');
            const registrationId = params.get('registrationId');
            const isNewUser = params.get('isNewUser') === 'true';

            console.log('email : ', email);

            if(email && registrationId){
                setFormData(t => ({
                    ...t
                    , email
                    , registrationId
                }));
                setIsSSO(true);
                dispatch(setUser({ email, registrationId, isNewUser }));
            }
        } 
    }, [location, dispatch]);

    useEffect(() => {
        if(isNewUser === false){
            navigate('/')
        }
    }, [isNewUser, navigate]);

    const handleOnChange = (e) => {
        const { name, value } = e.target;
        if(name === 'pwConfirm'){
            setPwConfirm(value);
        } else {
            setFormData(t => ({ ...t, [name]: value }));
        }    
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if(!dataCheck()){
            return ;
        }

        setIsLoading(true);

        try {
            const dataSubmit = {...formData};

            if(isSSO){
                delete dataSubmit.userPw;
            }

            const response = await api.post('/auth/sign1/domSignup', dataSubmit);
            if(response.data.message === "Signup successful"){
                alert('가입되었습니다.');
                navigate('/');
            } else {
                setError("회원가입 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            }
        } catch (error) {
            console.log('Signup failed:', error);
            setError(error.response?.data?.message || "회원가입 처리 중 오류가 발생했습니다.");
        } finally {
            setIsLoading(false);
        }
    };

    const LoadingSpinner = () => (
        <div className="flex items-center justify-center w-56 h-56 border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-800 dark:border-gray-700">
            <div role="status">
                <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/><path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/></svg>
                <span className="sr-only">Loading...</span>
            </div>
        </div>
    );

    return(
        <form onSubmit={handleSubmit} className="flex flex-col items-center">
            <div>{isSSO ? 'SNS 간편가입' : '일반 회원가입'}</div>
            <hr className="w-full my-4"></hr>
            <div>
                <span>아이디 </span>
                <span>*</span>
            </div>
            <div>
                <input name="userId" value={formData.userId} onChange={handleOnChange} placeholder="아이디를 입력해 주세요" className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/>
            </div>

            {!isSSO && (
                <>
                    <div>
                        <span>비밀번호 </span>
                        <span>*</span>
                    </div>
                    <div>
                        <input name="userPw" type="password" value={formData.userPw} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="비밀번호를 입력해 주세요."/>
                    </div>
                    <div>
                        <span>비밀번호 확인 </span>
                        <span>*</span>
                    </div>
                    <div>
                        <input name="pwConfirm" type="password" value={pwConfirm} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="비밀번호를 한 번 더 입력해 주세요."/>
                    </div>
                </>
            )}

            <div>
                <span>이름 </span>
                <span>*</span>
            </div>
            <div>
                <input name="userName" value={formData.userName} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="이름을 입력해 주세요."/>
            </div>
            <div>
                <span>휴대번호 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input name="userTel" value={formData.userTel} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" placeholder="숫자만 입력해 주세요."/></span>
                <button>인증번호 발송</button>
            </div>
            <div>
                <span>이메일 </span>
                <span>*</span>
            </div>
            <div>
                <span>
                    <input name="email" value={formData.email} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly={isSSO} placeholder={formData.email || '이메일을 입력해 주세요.'}/></span>
            </div>
            <div>
                <span>생년월일/성별 </span>
                <span>*</span>
            </div>
            <div className="flex">
                <span><input name="birthDate" type="date" value={formData.birthDate} onChange={handleOnChange} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"/></span>
                <span>
                    <input name="gender" type="radio" value="male" onChange={handleOnChange} checked={formData.gender === 'male'}/> 남성
                    <input name="gender" type="radio" value="female" onChange={handleOnChange} checked={formData.gender === 'female'}/> 여성
                </span>
            </div>
            <div className="flex flex-col items-center">
                {error && <div className="text-red-500 mb-2">{error}</div>}
                <button type="submit" 
                        className="flex items-center justify-center w-full py-2 px-3 text-white bg-blue-600 rounded shadow hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 disabled:opacity-50" 
                        disabled={isLoading}>
                        {isLoading ? (
                            <>
                                <LoadingSpinner />
                                <span className="ml-2">처리중...</span>
                            </>
                        ) : (
                            '회원가입'
                        )}
                </button>
            </div>
        </form>
    )
};

export default DomSignup;