import { type } from "@testing-library/user-event/dist/type";
import { LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT } from "../actionType";

export const loginSuccess = (user) => ({
    type: LOGIN_SUCCESS
    , payload: user
});

export const loginFailure = (error) => ({
    type: LOGIN_FAILURE
    , payload: error
});

export const logout = () => ({
    type: LOGOUT
});