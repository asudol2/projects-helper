import {Global} from "../config/Config";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { LoginResponse } from "../model/LoginResponse";
import { UserDataResponse } from "../model/UserDataResponse";

function fetchGet(url: string, token: string = "", secret: string = "") {
    const concatenatedValue = `${token}:${secret}`;
    const base64EncodedValue = btoa(concatenatedValue);
    return fetch(Global.backendUrl + url, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${base64EncodedValue ?? ""}`
        }
    })
}

function fetchPost(url: string, body: any, token: string = "", secret: string = "") {
    const concatenatedValue = `${token}:${secret}`;
    const base64EncodedValue = btoa(concatenatedValue);
    return fetch(Global.backendUrl + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${base64EncodedValue ?? ""}`
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
        const response = await fetchGet("/name", token, secret);
        if (response.status !== 200) {
            return {err: "błąd"}
        }
    
        const json = await response.json();
        return {res: json};
    }

    static async getOAuthCredentials() {
        let loginToken = SecurityHelper.getLoginToken();
        let response: Response | null = null;
        response = await fetchPost("/oauthcredentials", { loginToken: loginToken });
        if (response == null || response.status !== 200) {
            throw new Error();
        }
        const json = await response.json();
        return {res: json}
    }

    static async getAllCourses(token: string, secret: string) {
        const response = await fetchGet("/courses", token, secret);
        if (response.status !== 200) {
            throw new Error();
        }
        const json = await response.json();
        return { res: json }
    }

    static async getCourseTopics(token: string, secret: string, courseId: string) {   //TODO przemyśleć reużywalność kodu
        const response = await fetchGet("/topics?course_id="+courseId, token, secret);
        if (response.status !== 200) {
            throw new Error();
        }
        const json = await response.json();
        return { res: json }
    }

    static async getTopicById(token: string, secret: string, topicId: string) {
        const response = await fetchGet("/topics/" + topicId, token, secret);
        if (response.status !== 200) {
            throw new Error();
        }
        const json = await response.json();
        return { res: json }
    }

    static async addTopic(token: string, secret: string, courseId: string, title: string, description: string) {
        const response = await fetchPost(
                    "/topics/add", { courseId: courseId, title: title, description: description}, token, secret);
        if (response == null || response.status !== 200) {
            throw new Error();
        }
        const json = await response.json();
        return { res: json }
    }
}