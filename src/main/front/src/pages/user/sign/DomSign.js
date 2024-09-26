import { useLocation } from "react-router-dom";

const DomSign = () => {
    const location = useLocation();
    const { email, registrationId } = location.state || {};

    return(
        <div className="flex">
            <div>
                <div>SNS 간편가입</div>
            </div>
            <div>
                <hr></hr>
            </div>
            <div>
                <div>회원정보 입력</div>
            </div>
            <div>
                <span>아이디 </span>
                <span>*</span>
            </div>
            <div>
                <input className={`${email  != null ? `placeholder:${email} disabled` : `placeholder:아이디를 입력해 주세요.`} `}/>
            </div>
            <div>
                <span>비밀번호 </span>
                <span>*</span>
            </div>
            <div>
                <input className="placeholder:비밀번호를 입력해 주세요."/>
            </div>
            <div>
                <span>비밀번호 확인 </span>
                <span>*</span>
            </div>
            <div>
                <input className="placeholder:비밀번호를 한 번 더 입력해 주세요."/>
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
                <span><input className={`${email  != null ? `placeholder:${email} disabled` : `placeholder:이메일을 입력해 주세요.`} `}/></span>
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

export default DomSign;