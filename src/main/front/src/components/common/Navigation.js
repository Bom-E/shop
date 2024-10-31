import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { selectIsLoggedIn, selectUser } from "../../redux/reducers/authSlice";
import { handleLogout } from "../../utils/auth";

const Navigation = ({navItems, isActive, navigateTo, className = ''}) => {
    const dispatch = useDispatch();
    const isLoggedIn = useSelector(selectIsLoggedIn);
    const user = useSelector(selectUser);

    const onLogout = () => {
        handleLogout(dispatch, navigateTo)();
    }

    return(
        <nav className={`border-gray-200 dark:bg-gray-900 mt-auto ${className}`}>
            <div className="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto p-4">
                <ul className="font-medium flex flex-row justify-start space-x-8 mt-0 border-0">
                    {navItems.map(({path, label}) => (
                        <li key={path}>
                            <button
                                onClick={() => navigateTo(path)}
                                className={`block py-2 px-3 ${
                                    isActive(path)
                                        ? 'text-white rounded md:bg-transparent md:text-blue-700 md:p-0 dark:text-wrap md:dark:text-blue-500'
                                        : 'text-gray-900 rounded hover:bg-gray-100 md:hover:bg-transparent md:border-0 md:hover:text-blue-700 md:p-0 dark:text-white md:dark:hover:text-blue-500 dark:hover:bg-gray-700 dark:hover:text-white md:dark:hover:bg-transparent'
                                } appearance-none focus:outline-none`} aria-current={isActive(path) ? 'page' : undefined}>
                                {label}
                            </button>  
                        </li>
                    ))}
                </ul>
                <div className="flex space-x-4">
                    {isLoggedIn ? (
                        <>
                            <span className="px-4 py-2 text-sm font-medium text-gray-500">
                                {user?.userId}님
                            </span>
                            <button onClick={onLogout} className="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700">
                                로그아웃
                            </button>
                        </>
                    ) : (
                        <>
                            <button onClick={() => navigateTo('/auth/login')} className="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700">
                                로그인
                            </button>
                            <button onClick={() => navigateTo('/auth/sign1')} className="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700">
                                회원가입
                            </button>
                        </>
                    )}
                    
                    <button className="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700">
                        마이페이지
                    </button>
                    <button className="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700">
                        주문내역
                    </button>
                </div>
            </div>
        </nav>

    );
}

export default Navigation;