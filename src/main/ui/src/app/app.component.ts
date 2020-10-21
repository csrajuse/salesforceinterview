import { Component, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AppService } from './app.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {

  constructor(private appService: AppService) {}

  title = 'angular-nodejs-example';

  accountForm = new FormGroup({
    name: new FormControl('', Validators.nullValidator && Validators.required),
    type: new FormControl('', Validators.nullValidator && Validators.required),
    phone: new FormControl('', Validators.nullValidator && Validators.required),
    parentId: new FormControl('', Validators.nullValidator && Validators.required),
  });

  accounts: any[] = [];
  accountCount = 0;

  destroy$: Subject<boolean> = new Subject<boolean>();

  onSubmit() {
    this.appService.addAccount(this.accountForm.value).pipe(takeUntil(this.destroy$)).subscribe(data => {
      console.log('message::::', data);
      this.accountCount=this.accountCount+1;
      console.log(this.accountCount);
      this.accountForm.reset();
    });
  }

  getAllAccounts() {
    this.appService.getAccounts().pipe(takeUntil(this.destroy$)).subscribe((accounts: any[]) => {
		this.accountCount = accounts.length;
        this.accounts = accounts;
    });
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  ngOnInit() {
	this.getAllAccounts();
  }
}
