import {Global} from "../config/Config";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { LoginResponse } from "../model/LoginResponse";
import { UserDataResponse } from "../model/UserDataResponse";

function fetchGet(url: string) {
    return fetch(Global.backendUrl + url, {
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': `${token ?? ""}`
        }
    })
}

function fetchPost(url: string, body: any) {
    return fetch(Global.backendUrl + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': `${token ?? ""}`
        },
        body: JSON.stringify(body)
    })
}

export type ErrorResponse = {
    msg: string
}

class GenericResponse <T>{
    res?: T = undefined
    err?: string = undefined
}

export class Requests {
    static async login(): Promise<GenericResponse<LoginResponse>> {
        const response = await fetchGet("/login")

        if (response.status !== 200) {
            return {err: "błąd"}
        }
    
        const json = await response.json();
        return {res: json};
    }
    static async getUserData(token: string, secret: string): Promise<GenericResponse<UserDataResponse>> {
        const response = await fetchGet("/name?token="+token+"&secret="+secret) //TODO poprawić
        if (response.status !== 200) {
            return {err: "błąd"}
        }
    
        const json = await response.json();
        return {res: json};
    }

    static async getOAuthCredentials() {
        let loginToken = SecurityHelper.getLoginToken();
        const response = await fetchPost("/oauthcredentials", {loginToken: loginToken});
        if (response.status !== 200) {
            return {err: "Błąd"}
        }
        const json = await response.json();
        return {res: json}
    }

    static async getAllCourses(token: string, secret: string) {
        const response = await fetchGet("/courses?token=" + token + "&secret=" + secret);
        if (response.status !== 200) {
            return { err: "Błąd" }
        }
        const json = await response.json();
        return { res: json }
    }
}