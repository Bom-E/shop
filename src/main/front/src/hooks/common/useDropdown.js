import { useState, useRef, useCallback } from "react";
import useClickOutside from './useClickOutside';

const useDropdown = () => {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);

    const toggle = useCallback(() => setIsOpen(prev => !prev), []);
    const close = useCallback(() => setIsOpen(false), []);

    useClickOutside(dropdownRef, close);

    return{
        isOpen
        , toggle
        , dropdownRef
    };
};

export default useDropdown;