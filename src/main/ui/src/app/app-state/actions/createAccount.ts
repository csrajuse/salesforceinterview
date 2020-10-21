import { createAction, props } from '@ngrx/store';
import { Account } from '../models';

export const CREATE_ACCOUNT = '[CREATE ACCOUNT] Create Account API ';
export const CREATE_ACCOUNT_SUCCESS = '[CREATE ACCOUNT] Create Account API Success';
export const CREATE_ACCOUNT_FAILURE = '[CREATE ACCOUNT] Create Account API Failure';

export const createAccount = createAction(
  CREATE_ACCOUNT,
  props<Account>()
);

export const createAccountSuccess = createAction(
  CREATE_ACCOUNT,
  props<Account>()
);

export const createAccountFailure = createAction(
  CREATE_ACCOUNT,
  props<Account>()
);
