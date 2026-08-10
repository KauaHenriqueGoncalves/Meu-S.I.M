export interface CreateSchoolPlanRequestDto {
    name: string;
    monthlyPrice: number;
    maxStudents: number;
    maxCollaborators: number;
    maxLegalGuardian: number;
    maxSchoolAdmin: number;
}