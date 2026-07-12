export interface CreateClassroomRequestDto {
    classTypeId: number;
    subjectId: string;
    maxStudents: number;
    name: string;
    description: string;
}