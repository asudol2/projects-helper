import { CourseParticipant } from "../model/CourseParticipant";

export type TeamRequestResponse = {
    teamId: number;
    topicId: number;
    topicTitle: string;
    courseName: string;
    participants: CourseParticipant[];
}
