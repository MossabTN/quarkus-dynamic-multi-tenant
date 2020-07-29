import {BrowserModule} from '@angular/platform-browser';
import {DoBootstrap, NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule, HttpHeaders} from '@angular/common/http';
import {ClarityModule} from '@clr/angular';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';

import {AppRoutingModule} from './app-routing.module';

import {environment} from '../environments/environment';
import {AppComponent} from "./app.component";
import {HomeComponent} from "./home/home.component";
import {AuthInterceptor} from "./core/interceptor/auth.interceptor";
import {FooterComponent, NavbarComponent} from "./layouts";
import {TasksComponent} from "./components/tasks/container/tasks.component";
import {TaskService} from "./components/tasks/services/task.service";

let keycloakService: KeycloakService = new KeycloakService();

@NgModule({
    declarations: [
        AppComponent,
        NavbarComponent,
        FooterComponent,
        TasksComponent,
        HomeComponent],
    imports: [
        BrowserModule,
        HttpClientModule,
        ClarityModule,
        KeycloakAngularModule,
        AppRoutingModule
    ],
    providers: [
        TaskService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: KeycloakService,
            useValue: keycloakService
        }
    ],
    entryComponents: [AppComponent]
})
export class AppModule implements DoBootstrap {

    constructor(private http: HttpClient) {
    }

    async ngDoBootstrap(app) {
        const {keycloak, apis} = environment;

        try {
            const domain = window.location.hostname;
            let tenant = 'tenant1';
            if (!(domain.indexOf('.') < 0 || domain.split('.')[0] === 'www')) tenant = domain.split('.')[0];

            await this.http.get(apis.backend + '/api/tenants/' + tenant + '/config', {headers: new HttpHeaders({'x-tenant': 'default'})})
                .toPromise()
                .then(
                    async (data: any) => {
                        keycloak.url = data.oidcUrl;
                        keycloak.realm = data.oidcRealm;
                        await keycloakService.init({config: keycloak})
                    },
                    error1 => console.error('Keycloak init failed', error1)
                );
            app.bootstrap(AppComponent);
        } catch (error) {
            console.error('Keycloak init failed', error);
        }
    }
}
