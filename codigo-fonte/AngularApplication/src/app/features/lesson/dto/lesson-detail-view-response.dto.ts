import { AttendanceViewResponseDTO } from "./attendance/attendance-view-response.dto";

export interface LessonDetailViewResponseDto {
    id: string;
    lessonDate: Date;
    responsibleId: string;
    responsibleUsername: string;
    responsibleRole: string;
    responsibleEmail: string;
    classroomId: string;
    classroomName: string;
    subjectName: string;
    scheduleId: string;
    weekday: number;
    startTime: Date;
    endTime: Date;
    description: string;
    createdAt: Date;
    attendances: AttendanceViewResponseDTO[];
}