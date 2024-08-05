import {useNavigate, useLocation} from 'react-router-dom';

const useNavigation = (navItems) => {
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = (path) => location.pathname === path;
    const navigateTo = (path) => navigate(path);

    return {isActive, navigateTo, navItems};
};

export default useNavigation;