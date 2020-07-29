import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AppConfig} from "../task.config";
import {Task} from "../model/task.model";

@Injectable()
export class TaskService {
  constructor(private http: HttpClient) {}

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(AppConfig.api+'me');
  }
}
