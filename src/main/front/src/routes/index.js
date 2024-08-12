import Home from '../pages/Home';
import Domestic from '../pages/Domestic';
import Foreign from '../pages/Foreign';
import Goods from '../pages/Goods';
import Events from '../pages/Events';
import UserSign from '../pages/user/UserSign';
import Sign1 from '../pages/user/sign/Sign1';
import Sign2 from '../pages/user/sign/Sign2';
import Sign3 from '../pages/user/sign/Sign3';

export const navItems = [
    {path : '/', label : 'Home', component : Home}
    , {path : '/domestic', label : '국내도서', component : Domestic}
    , {path : '/foreign' , label : '해외도서', component : Foreign}
    , {path : '/goods', label : '굿즈', component : Goods}
    , {path : '/events', label : '이벤트', component : Events}
    , {path : '/userSign', label : '회원가입', component : UserSign}
];

export const signItems = [
    {path : 'sign1', label : '14세 이상 가입', component : Sign1}
    , {path : 'sign2', label : '14세 미만 가입', component : Sign2}
    , {path : 'sign3' , label : '외국인 가입', component : Sign3}
];

const routes = [
    ...navItems
    , {
        path : '/userSign'
        , component : UserSign
        , children : signItems
    }
];

export default routes;