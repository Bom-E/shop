export const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    
    if(parts.length === 2){
        return parts.pop().split(';').shift();
    }
};

export const clearAuthCookies = () => {
    document.cookie = 'access_token=; expires=Thu, 05 May 1996 00:00:00 UTC; path=/;';
    document.cookie = 'refresh_token=; expires=Thu, 05 May 1996 00:00:00 UTC; path=/;';
}