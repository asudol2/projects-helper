import {Global} from "../config/Config";
import { LoginResponse } from "../model/LoginResponse";
import { UserDataResponse } from "../model/UserDataResponse";

function fetchGet(url: string) {
    // const token = SecurityHelper.getContext()?.token;
    return fetch(Global.backendUrl + url, {
        headers: {
            'Content-Type': 'application/json',
            // 'Authorization': `${token ?? ""}`
        }
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
            console.log("wyskakuje mi błą")
            return {err: "błąd"}
        }
    
        const json = await response.json();
        return {res: json};
    }
    static async getUserData(token :string, secret : string): Promise<GenericResponse<UserDataResponse>> {
        console.log("getUserData()")
        const response = await fetchGet("/name?token="+token+"&secret="+secret)
        if (response.status !== 200) {
            return {err: "błąd"}
        }
    
        const json = await response.json();
        return {res: json};
    }
}