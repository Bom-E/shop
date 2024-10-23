import GridSlider from "../../components/common/GridSlider";
import { ChevronRight } from 'lucide-react';
import toYou from '../assets/toYou.png';
import you from '../assets/you.png';
import middleEvent from '../assets/middleEvent.png';

const Home = () => {

    return(
    <div>
        <div>
            <GridSlider/>    
        </div>
        {/* 어제 베스트셀러 */}
        <div>
            <div>
                <div className="text-3xl font-bold mt-10 flex items-start justify-between">
                    <div className="flex items-center">
                        <span>어제 베스트셀러 TOP 10</span>
                        <ChevronRight className="ml-1"/>  
                    </div>
                </div>
            </div> 
            <div>
                <div className="grid grid-cols-3 gap-3 mt-5">
                    <div className="col-span-1 row-span-4">
                        <div className="flex mb-5">
                            <img src={you} alt="책1" className="w-28 h-27.8"/>
                            <div className="ml-2">
                                <span className="font-bold flex items-center justify-between">1</span>
                                <p className="items-center">당신이 누군가를 죽였다</p>
                            </div>
                        </div>
                        <div>
                            <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                        </div>
                        <div className="flex">
                            <img src={toYou} alt="책2" className="w-28 h-28.1"/>
                            <div className="ml-2">
                                <span className="font-bold">2</span>
                                <p>나에게 들려주는 딘단한 말</p>
                            </div>
                        </div>
                        <div>
                            <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                        </div>
                    </div>
                    <div className="col-span-1 row-span-4">
                        <div className="flex flex-col space-y-2">
                            <div className="mb-2">3. 저축노하우 식사법</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div className="mb-2">4. 미래를 먼저 경험했습니다</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div className="mb-2">5. 일인칭 가난</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div>6. 죽이고 싶은 아이 2</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                        </div>
                    </div>
                    <div className="col-span-1 row-span-4">
                        <div className="flex flex-col space-y-2">
                            <div className="mb-2">7. 더 좋은 문장을 쓰고 싶은...</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div className="mb-2">8. 불변의 법칙</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div className="mb-2">9. 지도로 보아야 보인다</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                            <div>10. ETS 토익 정기시험 기출문...</div>
                            <div>
                                <hr className="h-px my-4 bg-gray-200 border-0 dark:bg-gray-700"></hr>
                            </div>
                        </div>
                    </div>                   
                </div>
            </div>
        </div>
        <div className="mt-10 overflow-hidden w-full">
            <div className="overflow-x-auto w-full">
                <img src={middleEvent} alt="중간이벤트배너" className="w-max h-40"/>
            </div>
        </div>
        <div>
            추천 마법사!!!!!
        </div>
        <div>
            이달의 책 Top 10 (독자 투표 이벤트)
        </div>
    </div>
    )
};

export default Home;

