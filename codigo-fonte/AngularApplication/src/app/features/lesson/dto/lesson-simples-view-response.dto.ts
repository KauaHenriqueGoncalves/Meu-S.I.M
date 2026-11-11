import { LessonStatus } from "../enum/lesson-status.enum";

export interface LessonSimpleViewResponseDTO {
    id: string;
    lessonDate: Date;
    status: LessonStatus;
    weekday: number;
    startTime: Date;
    endTime: Date;
}