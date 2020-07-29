import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {KeycloakBearerInterceptor, KeycloakService} from "keycloak-angular";
import {environment} from "environments";

const {keycloak} = environment;

@Injectable()
export class AuthInterceptor extends KeycloakBearerInterceptor {

    constructor(private keycloakService: KeycloakService) {
        super(keycloakService)
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        const tenant = request.headers.get('x-tenant');
        if (tenant) {
            return super.intercept(request, next);
        }
        return super.intercept(request.clone({
            headers: request.headers.set('x-tenant', keycloak.realm)
        }), next);
    }

}
