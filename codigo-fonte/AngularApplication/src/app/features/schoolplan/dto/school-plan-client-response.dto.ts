import { Decimal } from "decimal.js";

export interface SchoolPlanClientResponseDto {
    id: string,
    name: string,
    basePrice: Decimal,
    monthlyPrice: Decimal,
    maxStudents: number,
    maxCollaborators: number,
    maxLegalGuardian: number,
    maxSchoolAdmin: number,
    selectedMonths: number,
    isLoading?: boolean
}
