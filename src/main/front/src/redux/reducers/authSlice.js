import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    isLoggedIn: false
    , user: null
    , error: null
    , isNewUser: false
    , registrationData: null
    , accessToken: null
    , refreshToken: null
};

const authSlice = createSlice({
    name: 'auth'
    , initialState
    , reducers: {
        loginSuccess: (state, action) => {
            state.isLoggedIn = true;
            state.user = action.payload;
            state.error = null;
        },
        loginFailure: (state, action) => {
            state.isLoggedIn = false;
            state.user = null;
            state.error = action.payload;
        },
        logout: (state) => {
            state.isLoggedIn = false;
            state.user = null;
            state.error = null;
            state.isNewUser = false;
            state.registrationId = null;
        }
        , setRegistrationData: (state, action) => {
            state.isLoggedIn = true;
            state.registrationData = action.payload;
        }
        , clearRegistrationData: (state) => {
            state.isNewUser = false;
            state.registrationData = null;
        }
        , registrationSuccess: (state, action) => {
            state.isLoggedIn = true;
            state.user = action.payload;
            state.isNewUser = false;
            state.registrationData = null;
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
            state.isNewUser = action.payload.isNewUser;
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

export default authSlice.reducer;