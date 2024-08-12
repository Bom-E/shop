import React from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { signItems } from "../../routes";

const UserSign = ({ className = '' }) => {
    const location = useLocation();
    const navigate = useNavigate();

    const checkActive = (path) => location.pathname.endsWith(path);
    const signNavi = (path) => navigate(`/userSign/${path}`);

    return(
        <div>
            <nav className={`border-gray-200 dark:bg-gray-900 mt-auto ${className}`}>
                <div className="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto p-4">
                    <ul className="font-medium flex flex-row justify-start space-x-8 mt-0 border-0">
                        {signItems.map(({path, label}) => (
                            <li key={path}>
                                <button
                                    onClick={() => signNavi(path)}
                                    className={`block py-2 px-3 ${
                                        checkActive(path)
                                            ? 'text-white rounded md:bg-transparent md:text-blue-700 md:p-0 dark:text-wrap md:dark:text-blue-500'
                                            : 'text-gray-900 rounded hover:bg-gray-100 md:hover:bg-transparent md:border-0 md:hover:text-blue-700 md:p-0 dark:text-white md:dark:hover:text-blue-500 dark:hover:bg-gray-700 dark:hover::text-white md:dark:hover:bg-transparent'
                                        } appearance-auto`} aria-current={checkActive(path) ? 'page' : undefined}>
                                    {label}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </nav>
            <Outlet/>
        </div>
    );
}

export default UserSign;