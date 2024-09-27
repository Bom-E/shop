import { combineReducers } from 'redux';
import authSlice from './reducers/authSlice';

const rootReducer = combineReducers({
    auth : authSlice
});

export default rootReducer;