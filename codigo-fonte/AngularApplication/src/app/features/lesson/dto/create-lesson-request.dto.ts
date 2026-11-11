import { CreateAttendanceRequestDTO } from "./attendance/create-attendance-request.dto";

export interface CreateLessonRequestDto {
    scheduleId: string;
    classroomId: string;
    lessonDate: string;
    isCanceled: boolean;
    description: string;
    attendances: CreateAttendanceRequestDTO[];
}