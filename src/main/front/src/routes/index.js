import Home from '../pages/Home';
import Domestic from '../pages/Domestic';
import Foreign from '../pages/Foreign';
import Goods from '../pages/Goods';
import Events from '../pages/Events';
import Auth from '../pages/user/Auth';
import Sign1 from '../pages/user/sign/Sign1';
import Sign2 from '../pages/user/sign/Sign2';
import { Navigate } from 'react-router-dom';
import DomSignup from '../pages/user/sign/DomSignup';
import ForSignup from '../pages/user/sign/ForSignup';
import LoginPage from '../pages/user/login/LoginPage';


export const navItems = [
    {path : '/', label : 'Home', component : Home}
    , {path : '/domestic', label : '국내 도서', component : Domestic}
    , {path : '/foreign' , label : '해외 도서', component : Foreign}
    , {path : '/goods', label : '굿즈', component : Goods}
    , {path : '/events', label : '이벤트', component : Events}
];

export const signItems = [
    {path : 'sign1', label : '국내 회원', component : Sign1}
    , {path : 'sign2', label : '해외 회원', component : Sign2}
];

const routes = [
    ...navItems
    , {
        path : 'auth'
        , element : <Auth />
        , children : [
            { index : true, element : <Navigate to="sign1" replace /> }
            , ...signItems.map(item => ({
                path: item.path
                , element : <item.component />
            }))
        ]
    }
    , { path: 'auth/login', element: <LoginPage /> }
    , { path: 'auth/sign1/domSignup', element: <DomSignup /> }
    , { path: 'auth/sign1/signup', element: <DomSignup /> }
    , { path: 'auth/sign2/forSignup', element: <ForSignup /> }

];

export default routes;