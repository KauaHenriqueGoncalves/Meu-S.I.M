export interface UpdateSchoolPlanRequestDto {
    name: string;
    monthlyPrice: number;
    maxStudents: number;
    maxCollaborators: number;
    maxLegalGuardian: number;
    maxSchoolAdmin: number;
    isActive: boolean;
}