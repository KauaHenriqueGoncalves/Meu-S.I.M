import { LegalGuardianViewSimpleResponseDto } from "../../legalguardian/dto/legal-guardian-view-simple-response.dto";

export interface StudentDetailRequestDto {
    id: string,
    name: string,
    dateOfBirth: Date,
    grade: string,
    legalGuardianResponse: LegalGuardianViewSimpleResponseDto
}