import { CourseParticipant } from "../model/CourseParticipant";

export type TeamRequestResponse = {
    topicId: number;
    topicTitle: string;
    courseName: string;
    participants: CourseParticipant[];
}
