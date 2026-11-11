import { NoteType } from "../../enum/note-type.enum";

export interface LessonNoteViewResponseDTO {
    id: string;
    type: NoteType;
    subType: string;
    content: string;
}