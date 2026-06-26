export interface UpdateCollaboratorRequestDto {
    username: string;
    email: string;
    phoneNumber: string;
    address: string;
    isActive: boolean;
    dateOfBirth: Date;
    specialty: string;
    workload: string;
}