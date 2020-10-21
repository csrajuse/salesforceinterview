import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) { }

  rootURL = '/api';

  getAccounts() {
    return this.http.get(this.rootURL + '/accounts');
  }

  addAccount(account: any) {
	return this.http.post(this.rootURL + '/account', account);
  }

}
