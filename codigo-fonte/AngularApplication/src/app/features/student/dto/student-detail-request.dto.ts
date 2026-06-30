export interface StudentDetailRequestDto {
    id: string,
    name: string,
    dateOfBirth: Date,
    grade: string,
    legalGuardian: LegalGuardianResponse
}

interface LegalGuardianResponse {
    id: string,
    username: string,
    degreeOfKinship: string
}