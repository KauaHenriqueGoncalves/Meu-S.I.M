import { NoteType } from "../../enum/note-type.enum";

export interface CreateLessonNoteRequestDTO {
    type: NoteType;
    subtype: string;
    content: string;
}