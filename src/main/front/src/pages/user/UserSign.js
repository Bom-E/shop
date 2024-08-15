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
            <nav className={`border-gray-200 dark:bg-gray-900 flex items-center justify-center h-16 ${className}`}>
                <div className="max-w-screen-xl flex flex-wrap items-center justify-center mx-auto p-4">
                    <ul className="font-medium flex w-full justify-between items-center space-x-0 mt-0 border-0">
                        {signItems.map(({path, label}) => (
                            <li key={path} className="flex-1 h-full">
                                <button
                                    onClick={() => signNavi(path)}
                                    className={`w-full h-20 flex items-center justify-center mr-40 ${
                                        checkActive(path)
                                            ? 'text-white bg-blue-700'
                                            : 'bg-white text-gray-900 hover:bg-gray-100'
                                        } appearance-auto`} aria-current={checkActive(path) ? 'page' : undefined}>
                                    {label}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </nav>
            <hr className="mt-2"></hr>
            <Outlet/>
        </div>
    );
}

export default UserSign;