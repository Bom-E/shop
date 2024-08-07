import { useState, useEffect, useMemo } from "react";
import slide_1 from '../../assets/slide_1.png';
import slide_2 from '../../assets/slide_2.png';
import slide_3 from '../../assets/slide_3.png';
import slide_4 from '../../assets/slide_4.png';

const GridSlider = () => {
    const [activeSlide, setActiveSlide] = useState(0);

    const slides = useMemo(() => [
        {
            main: [
                { image : slide_1 }
                , { image : slide_2 }
                , { image : slide_3 }
                , { image : slide_4 }
            ]
            , items: [
                { image : "https://flowbite.s3.amazonaws.com/docs/gallery/square/image-1.jpg", text : "국내도서" }
                , { image : "https://flowbite.s3.amazonaws.com/docs/gallery/square/image-2.jpg", text : "해외도서" }
                , { image : "https://flowbite.s3.amazonaws.com/docs/gallery/square/image-3.jpg", text : "굿즈" }
                , { image : "https://flowbite.s3.amazonaws.com/docs/gallery/square/image-4.jpg", text : "이벤트" }
                ],
            },
        {}
    ], []);

    useEffect(() => {
        const interval = setInterval(() => {
            setActiveSlide((prev) => (prev + 1) % slides[0].main.length)
        }, 5000);

        return() => clearInterval(interval);
    }, [slides]);

    const handleItemClick = (index) => {
        setActiveSlide(index);
    };

    return(
        <div className="relative w-full overflow-hidden max-w-7xl mx-auto">
            <div className="flex flex-col">
                <div className="w-full mb-4">
                    <div className="relative overflow-hidden" style={{height : '400px'}}>
                        <div className="absolute inset-0 flex transition-transform duration-300 ease-in-out" style={{transform: `translateX(-${activeSlide * 100}%)`}}>
                            {slides[0].main.map((slide, index) => (
                                <div key={index} className="w-full flex-shrink-0">
                                    <img className="h-full w-full object-cover object-center" 
                                        src={slide.image} 
                                        alt={`Slide ${index + 1} main`}
                                    />
                                </div>
                            ))}
                        </div>
                    </div>
                    
                </div>
            </div>
            
            <div className="w-full mb-4">
                <div className="grid grid-cols-4 gap-4">
                    {slides[0].items.map((item, itemIndex) => (
                        <button key={itemIndex} 
                            onClick={() => handleItemClick(itemIndex)} 
                            className={`relative w-full rounded-md overflow-hidden focus:outline-none ${activeSlide === itemIndex ? 'ring-2 ring-blue-500' : ''}`}
                            style={{
                                paddingBottom : '25%'
                                , backgroundImage : `url(${item.image})`
                                , backgroundSize : 'cover'
                                , backgroundPosition : 'center'
                            }}
                        >
                            <div className="absolute inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center">
                                <span className="text-white text-lg font-bold">{item.text}</span>
                            </div>
                        </button>
                    ))}
                </div>
            </div>
            
        </div>
    )
};

export default GridSlider;