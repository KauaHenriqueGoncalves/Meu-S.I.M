import { LessonNoteViewResponseDTO } from "../lessonnote/lesson-note-view-response.dto";

export interface AttendanceViewResponseDTO {
    id: string;
    studentId: string;
    studentName: string;
    status: string;
    content: string;
    lessonNote: LessonNoteViewResponseDTO | null;
}