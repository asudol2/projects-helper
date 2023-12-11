import { Global } from "../config/Config";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { Course } from "../model/Course";
import { LoginResponse } from "../model/LoginResponse";
import { TokenResponse } from "../model/TokenRespone";
import { UserDataResponse } from "../model/UserDataResponse";
import { Topic } from "../model/Topic";

async function fetchGet(url: string, token: string = "", secret: string = "") {
    const concatenatedValue = `${token}:${secret}`;
    const base64EncodedValue = btoa(concatenatedValue);
    const response = await fetch(Global.backendUrl + url, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${base64EncodedValue ?? ""}`
        }
    })
    if (response.status !== 200) {
        return { err: "błąd" }
    }

    const json = await response.json();
    return { res: json };
}

async function fetchPost(url: string, body: any, token: string = "", secret: string = "") {
    const concatenatedValue = `${token}:${secret}`;
    const base64EncodedValue = btoa(concatenatedValue);
    const response = await fetch(Global.backendUrl + url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${base64EncodedValue ?? ""}`
        },
        body: JSON.stringify(body)
    })
    if (response == null || response.status !== 200) {
        throw new Error();
    }
    const json = await response.json();
    return { res: json }
}

export type ErrorResponse = {
    msg: string
}

class GenericResponse<T> {
    res?: T = undefined
    err?: string = undefined
}

export class Requests {
    static async login(): Promise<GenericResponse<LoginResponse>> {
        return await fetchGet("/login")
    }
    static async getUserData(token: string, secret: string): Promise<GenericResponse<UserDataResponse>> {
        return await fetchGet("/name", token, secret);
    }

    static async getOAuthCredentials(): Promise<GenericResponse<TokenResponse>> {
        let loginToken = SecurityHelper.getLoginToken();
        return await fetchPost("/oauthcredentials", { loginToken: loginToken });
    }

    static async getAllCourses(token: string, secret: string): Promise<GenericResponse<Course[]>> {
        return await fetchGet("/courses", token, secret);
    }

    static async getCourseTopics(token: string, secret: string, courseId: string): Promise<GenericResponse<Topic[]>> {
        return await fetchGet("/topics?course_id=" + courseId, token, secret);
    }

    static async getTopicById(token: string, secret: string, topicId: string): Promise<GenericResponse<Topic>> {
        return await fetchGet("/topics/" + topicId, token, secret);
    }

    static async addTopic(token: string, secret: string, courseId: string, title: string, description: string)
            : Promise<GenericResponse<string>> {
        return await fetchPost(
            "/topics/add", { courseId: courseId, title: title, description: description }, token, secret);
    }
}
