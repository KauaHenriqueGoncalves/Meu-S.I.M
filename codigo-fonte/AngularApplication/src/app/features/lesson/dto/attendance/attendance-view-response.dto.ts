import { LessonDetailViewResponseDto } from "../lesson-detail-view-response.dto";

export interface AttendanceViewResponseDTO {
    id: string;
    studentId: string;
    studentName: string;
    status: string;
    content: string;
    lessonNote: LessonDetailViewResponseDto | null;
}