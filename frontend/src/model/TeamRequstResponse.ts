import { CourseParticipant } from "../model/CourseParticipant";

export type TeamRequestResponse = {
    teamRequestId: number;
    topicId: number;
    topicTitle: string;
    courseName: string;
    participants: CourseParticipant[];
}
