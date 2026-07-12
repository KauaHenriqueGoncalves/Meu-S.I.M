export interface UpdateLegalGuardianRequestDto {
    username: string;
    email: string;
    phoneNumber: string;
    address: string;
    isActive: boolean;
    degreeOfKinship: string;
}