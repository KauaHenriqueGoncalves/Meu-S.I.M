import { UserRequestDto } from "../../user/dto/user-request.dto";
import { LegalGuardianRequestDto } from "./legal-guardian-resquest.dto";

export interface CreateLegalGuardianRequestDto {
    userRequest: UserRequestDto;
    legalGuardianRequest: LegalGuardianRequestDto;
}