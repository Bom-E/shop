import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    isLoggedIn: false
    , user: null
    , error: null
    , isNewUser: false
    , registrationId: null
    , accessToken: null
    , refreshToken: null
    , isSSO: false
};

const authSlice = createSlice({
    name: 'auth'
    , initialState
    , reducers: {
        loginSuccess: (state, action) => {
            state.isLoggedIn = true;
            state.user = action.payload;
            state.error = null;
        }
        , loginFailure: (state, action) => {
            state.isLoggedIn = false;
            state.user = null;
            state.error = action.payload;
        }
        , logout: (state) => {
            state.isLoggedIn = false;
            state.user = null;
            state.error = null;
            state.isNewUser = false;
            state.registrationId = null;
        }
        , setRegistrationId: (state, action) => {
            state.isLoggedIn = true;
            state.registrationId = action.payload;
        }
        , clearRegistrationId: (state) => {
            state.isNewUser = false;
            state.registrationId = null;
        }
        , registrationSuccess: (state, action) => {
            state.isLoggedIn = true;
            state.user = action.payload;
            state.isNewUser = false;
            state.registrationId = null;
            state.error = null;
        }
        , registrationFailure: (state, action) => {
            state.error = action.payload;
        }
        , setTokens: (state, action) => {
            state.accessToken = action.payload.accessToken;
            state.refreshToken = action.payload.accessToken;
        }
        , setUser: (state, action) => {
            state.user = action.payload;
            state.isNewUser = action.payload.isNewUser || false;
            state.isSSO = !!action.payload.registrationId;
            if(action.payload.registrationId){
                state.registrationId = action.payload.registrationId;
            }
        }
    }
})

export const { 
    loginSuccess
    , loginFailure
    , logout
    , setRegistrationData
    , clearRegistrationData
    , registrationSuccess
    , registrationFailure
    , setTokens
    , setUser
} = authSlice.actions;

export const selectAuth = (state) => state.auth;
export const selectUser = (state) => state.auth.user;
export const selectIsLoggedIn = (state) => state.auth.isLoggedIn;
export const selectError = (state) => state.auth.error;
export const selectIsSSO = (state) => state.auth.isSSO;
export const selectToken = (state) => ({
    accessToken: state.auth.accessToken
    , refreshToken: state.auth.refreshToken
}); 

export default authSlice.reducer;