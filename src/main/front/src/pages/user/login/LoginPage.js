import { useNavigate } from 'react-router-dom';
import springLogo from '../../../assets/with_Spring_removebg.png'

const LoginPage = () => {
    const navigate = useNavigate();

    return (
        <div>
            <div>
                <img src={springLogo} alt="Spring Logo" onClick={() => navigate("/")} className="w-40 h-40 items-center object-contain"/>
            </div>
        </div>
    )
}

export default LoginPage;