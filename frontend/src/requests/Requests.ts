import { Global } from "../config/Config";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { Course } from "../model/Course";
import { LoginResponse } from "../model/LoginResponse";
import { TokenResponse } from "../model/TokenRespone";
import { UserDataResponse } from "../model/UserDataResponse";
import { Topic } from "../model/Topic";
import { CourseParticipant } from "../model/CourseParticipant";
import { TeamRequestResponse } from "../model/TeamRequstResponse";

async function fetchGet(url: string, token: string = "", secret: string = "", jsonResponse: boolean = true) {
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

    if (jsonResponse) {
        const json = await response.json();
        return { res: json }
    }
    const text = await response.text();
    return { res: text }
}

async function fetchPost(url: string, body: any, token: string = "", secret: string = "", jsonResponse: boolean = true) {
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
    if (jsonResponse) {
        const json = await response.json();
        return { res: json }
    }
    const text = await response.text();
    return { res: text }
}

export type ErrorResponse = {
    msg: string
}

class GenericResponse<T> {
    res?: T = undefined
    err?: string = undefined
}

export class Requests {
    static login(): Promise<GenericResponse<LoginResponse>> {
        return fetchGet("/login")
    }
    static getUserData(token: string, secret: string): Promise<GenericResponse<UserDataResponse>> {
        return fetchGet("/name", token, secret);
    }

    static getOAuthCredentials(): Promise<GenericResponse<TokenResponse>> {
        let loginToken = SecurityHelper.getLoginToken();
        return fetchPost("/oauthcredentials", { loginToken: loginToken });
    }

    static getAllCourses(token: string, secret: string): Promise<GenericResponse<Course[]>> {
        return fetchGet("/courses", token, secret);
    }

    static getCourseTopics(token: string, secret: string, courseId: string): Promise<GenericResponse<Topic[]>> {
        return fetchGet("/topics?course_id=" + courseId, token, secret);
    }

    static getTopicById(token: string, secret: string, topicId: string): Promise<GenericResponse<Topic>> {
        return fetchGet("/topics/" + topicId, token, secret);
    }

    static addTopic(token: string, secret: string, courseId: string, title: string, description: string)
        : Promise<GenericResponse<string>> {
        return fetchPost(
            "/topics/add", { courseId: courseId, title: title, description: description }, token, secret);
    }

    static getCourseParticipants(token: string, secret: string, courseId: string)
        : Promise<GenericResponse<CourseParticipant[]>> {
        return fetchGet("/courses/participants?course_id=" + courseId, token, secret);
    }

    static addTeamRequest(token: string, secret: string, courseId: string,
        title: string, participantsIds: string[])
        : Promise<GenericResponse<string>> {
        return fetchPost(
            "/projects/add_request",
            { courseID: courseId, title: title, userIDs: participantsIds },
            token,
            secret
        );
    }

    static getCurrentTerm(token: string, secret: string): Promise<GenericResponse<string>> {
        return fetchGet("/courses/term", token, secret, false);
    }

    static removeTopicRequest(token: string, secret: string, courseId: string, title: string)
        : Promise<GenericResponse<boolean>> {
        return fetchPost("/topics/confirm", { courseId: courseId, title: title, confirm: false }, token, secret, false);
    }

    static getUserTeamRequests(token: string, secret: string): Promise<GenericResponse<TeamRequestResponse[]>> {
        return fetchGet("/projects/user_requests", token, secret);
    }

    static getUserTeams(token: string, secret: string): Promise<GenericResponse<Map<string, CourseParticipant[]>>> {
        return fetchGet("");  //TODO dokończyć
    }

    static rejectTeamRequest(token: string, secret: string, teamRequestId: number) : Promise<GenericResponse<boolean>> {
        return fetchPost("/projects/reject", teamRequestId, token, secret, false);
    }
}
