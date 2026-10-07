import { LessonDisplayStatus } from "../enum/lesson-display-status.enum";

export interface AgendaDayResponseDto {
    lessonId: string | null;
    scheduleId: string;
    date: string;
    weekday: string;
    startTime: string;
    endTime: string;
    displayStatus: LessonDisplayStatus;
}