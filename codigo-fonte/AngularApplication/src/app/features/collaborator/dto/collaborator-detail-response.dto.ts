export interface CollaboratorDetailResponseDto {
    id: string;
    username: string;
    email: string;
    cpf: string;
    phoneNumber: string;
    address: string;
    isActive: boolean;
    dateOfBirth: Date;
    specialty: string;
    workload: string;
}