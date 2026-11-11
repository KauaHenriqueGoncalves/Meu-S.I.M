import { AttendanceStatus } from "../../enum/attendance-status.enum";
import { CreateLessonNoteRequestDTO } from "../lessonnote/create-lesson-note-request.dto";

export interface CreateAttendanceRequestDTO {
    studentId: string;
    studentName: string;
    status: AttendanceStatus;
    content: string;
    lessonNote: CreateLessonNoteRequestDTO | null;
}