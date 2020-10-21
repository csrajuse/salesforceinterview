import * as createAccountActions from '../actions/createAccount';
import { Action, createReducer, on } from '@ngrx/store';

export interface State {
    isLoading: boolean;
    isLoadingSuccess: boolean;
    accounts: any;
}

const initialState: State = {
    isLoading: false,
    isLoadingSuccess: false,
    accounts: []
};

export const createAccountReducer = createReducer(
  initialState,
  on(createAccountActions.createAccount, state => ({...state, isLoading: true, isLoadingSuccess: false, login: undefined})),
  on(createAccountActions.createAccountSuccess, (state, account) => ({...state, isLoading: false, isLoadingSuccess: true, account})),
  on(createAccountActions.createAccountFailure, (state, account) => ({...state, isLoading: false, isLoadingSuccess: true, account}))
);

export function reducer(state: State | undefined, action: Action) {
  return createAccountReducer(state, action);
}

export const getAccounts = (state: State) => {
    return {
      isLoading: state.isLoading,
      isLoadingSuccess: state.isLoadingSuccess,
      login: state.accounts };
};
