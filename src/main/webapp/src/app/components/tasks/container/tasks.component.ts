import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {TaskService} from "../services/task.service";
import {Task} from "../model/task.model";

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css']
})
export class TasksComponent implements OnInit{
  tasks$: Observable<Task[]>;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.tasks$ = this.taskService.getMyTasks();
  }
}
