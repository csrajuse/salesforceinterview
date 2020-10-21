import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-display-board',
  templateUrl: './display-board.component.html',
  styleUrls: ['./display-board.component.css']
})
export class DisplayBoardComponent implements OnInit {

  constructor() { }

  @Input() accountCount = 0;
  @Output() getAccountsEvent = new EventEmitter();

  ngOnInit(): void {
  }

  getAllAccounts() {
    this.getAccountsEvent.emit('get all Accounts');
  }
}
