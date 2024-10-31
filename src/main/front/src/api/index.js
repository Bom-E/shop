import axios from 'axios';
import store from '../redux/store';
import { handleLogout } from '../utils/auth';

const api = axios.create({
  withCredentials: true
  , baseURL: 'http://localhost:8081'
  , headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 추가
api.interceptors.request.use(
  (config) => {
    // 요청 전에 수행할 작업

    const state = store.getState();
    const accessToken = state.auth.accessToken;

    if(accessToken){
      // Bearer: 소지자라는 의미. JWT 인증 표준 형식, 서버에게 이 토큰의 소지자에게 권한을 부여해달라는 의미.
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 추가
api.interceptors.response.use(
  (response) => {
    // 응답 데이터를 가공
    return response;
  },
  async (error) => {
    // 오류 응답을 처리
    const originalRequest = error.config;

    // ._retry: 커스텀 프로퍼티. 토큰 갱신 시 무한 루프 방지, 이미 재시도한 요청인지 체크하는 플래그로 사용.
    if(error.response?.status === 401 && !originalRequest._retry){
      originalRequest._retry = true;

      try {
        const response = await api.get('/auth/checkToken');

        if(response.data.message === "Access token refreshed successfully"){
            // 토큰 갱신 성공 시 재요청
            return api(originalRequest);
        }
      } catch (refreshError){
        const dispatch = store.dispatch;
        await handleLogout(dispatch, () => {
          window.location.href = '/auth/login';
        })();
      }
    }

    return Promise.reject(error);
  }
);

export default api;