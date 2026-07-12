export interface UpdateClassroomRequestDto {
    classTypeId: number;
    subjectId: string;
    maxStudents: number;
    name: string;
    description: string;
}