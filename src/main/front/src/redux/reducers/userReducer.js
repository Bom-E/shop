import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    isLoggedIn: false
        , user: null
        , error: null
};

const userSlice = createSlice({
    name: 'user'
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
        }
    }
})

export const { loginSuccess, loginFailure, logout } = userSlice.actions;

export default userSlice.reducer;