import { ClassTypeResponseDto } from "../../classtype/dto/class-type-response.dto";
import { SubjectResponseDto } from "../../subject/dto/subject-response.dto";
import { ClassroomViewSimpleStudentResponseDto } from "./classroom-view-simple-student-response.dto";

export interface ClassroomDetailResponseDto {
    id: string;
    classType: ClassTypeResponseDto;
    subject: SubjectResponseDto;
    name: string;
    maxStudents: number;
    description: string;
    students: ClassroomViewSimpleStudentResponseDto[];
}