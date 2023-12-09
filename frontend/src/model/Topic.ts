export type Topic = {
    id: number;
    courseID: string;
    lecturerID: number;
    title: string;
    description: string;
    minTeamCap: number;
    maxTeamCap: number;
    temporary: boolean;
}