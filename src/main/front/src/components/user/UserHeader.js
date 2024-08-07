import React, { useRef, useState } from 'react';
import springLogo from '../../assets/with_Spring_removebg.png';
import topBanner from '../../assets/topBanner.png';
import headerAD from '../../assets/headerAD.png';
import useClickOutside from '../../hooks/common/useClickOutside';

const UserHeader = ({navHome}) => {

    // 드롭 다운 훅
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [selectedOption, setSelectedOption] = useState("통합검색");
    const dropdownRef = useRef(null);

    useClickOutside(dropdownRef, () => {
        if(isDropdownOpen) setIsDropdownOpen(false);
    });

    const toggleDropdown = (e) => {
        e.preventDefault();
        setIsDropdownOpen(!isDropdownOpen);
    };

    const handleOptionClick = (optionName) => {
        setSelectedOption(optionName);
        setIsDropdownOpen(false);
    };


    return(
        <header className="w-full h-48">
            <div className="h-10 w-full flex items-center justify-center bg-gray-100">
                <img src={topBanner} alt="최상단 배너"/>
            </div>
            <div className="container mx-auto px-4 py-8 md:py-10 lg:py-4">
                <div className="flex justify-between items-center">
                    <span className="flex-shrink-0 cursor-pointer" onClick={navHome}>
                        <img src={springLogo} alt="Spring 로고" className="h-16 w-16 md:h-24 md:w-24 lg:h-44 lg:w-44 object-contain" />
                    </span>
                    <span className="flex-grow flex justify-start">
                        <form className="w-full max-w-xl">
                            <div className="flex relative" ref={dropdownRef}>
                                <button id="dropdown-button" onClick={toggleDropdown} className="flex-shrink-0 z-10 w-32 inline-flex items-center py-2.5 px-4 text-sm font-medium text-center text-gray-900 bg-gray-100 border border-gray-300 rounded-l-lg hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600" type="button">{selectedOption}
                                    <svg className="w-2.5 h-2.5 ms-2.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 6">
                                    <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 4 4 4-4"/>
                                    </svg>
                                </button>
                                {isDropdownOpen && (
                                    <div id="dropdown" className="absolute top-full left-0 z-50 mt-1 bg-white divide-y divide-gray-100 rounded-lg shadow w-32">
                                        <ul className="py-2 text-sm text-gray-700 dark:text-gray-200" aria-labelledby="dropdown-button">
                                            <li>
                                                <button type="button" name="all" onClick={() => handleOptionClick('통합검색')} className="inline-flex w-full px-4 py-2 hover:bg-gray-100">통합검색</button>
                                            </li>
                                            <li>
                                                <button type="button" name="goods" onClick={() => handleOptionClick('굿즈')} className="inline-flex w-full px-4 py-2 hover:bg-gray-100">굿즈</button>
                                            </li>
                                            <li>
                                                <button type="button" name="author" onClick={() => handleOptionClick('저자')} className="inline-flex w-full px-4 py-2 hover:bg-gray-100">저자</button>
                                            </li>
                                            <li>
                                                <button type="button" name="publisher" onClick={() => handleOptionClick('출판사')} className="inline-flex w-full px-4 py-2 hover:bg-gray-100">출판사</button>
                                            </li>
                                        </ul>
                                </div>
                                )}
                                <div className="relative flex-grow">
                                    <input type="search" id="search-dropdown" name="searchValue" className="block p-2.5 pr-12 w-full z-20 text-sm text-gray-900 bg-gray-50 rounded-r-lg border-l-gray-50 border-l-2 border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-s-gray-700  dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500" placeholder="Search Mockups, Logos, Design Templates..." required />
                                    <input type="hidden" name="searchType" value={
                                        selectedOption === '통합검색' ? 'all' :
                                        selectedOption === '굿즈' ? 'goods' :
                                        selectedOption === '저자' ? 'author' :
                                        selectedOption === '출판사' ? 'publisher' : ''

                                    }/>
                                    {/* <button type="submit" className="absolute top-0 right-0 p-2.5 px-3 text-sm font-medium h-full text-gray-600 bg-transparent rounded-r-lg border-0 focus:ring-4 focus:outline-none"> */}
                                    <button type="submit" className="absolute right-2.5 inset-y-0 flex items-center text-gray-600  focus:ring-4 focus:outline-none font-medium rounded-lg text-sm px-4">
                                        <svg className="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"/>
                                        </svg>
                                        <span className="sr-only">Search</span>
                                    </button>
                                </div>
                            </div>
                        </form>
                    </span>
                    <span className="bg-transparent flex-shrink-0 lg:w-1/5">
                        <div className="bg-gray-200 h-28 w-full items-center justify-center hidden md:flex">
                            <img src={headerAD} alt="헤더 광고"/>
                        </div>
                        <div className="bg-gray-200 w-full py-2 text-center hidden">
                            모바일용 광고(우측에 작게 띄우기)
                        </div>
                    </span>
                </div>
            </div>
        </header>

    )


}

export default UserHeader;

