import { combineReducers } from 'redux';

const userSign = (state={}, action) => {
    switch(action.type){
        default:
        return state;
    }
};

const rootReducer = combineReducers({
    user : userSign
});

export default rootReducer;