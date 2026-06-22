import { UserRequestDto } from "../../user/dto/user-request.dto";
import { CollaboratorRequestDto } from "./collaborator-request.dto";

export interface CreateCollaboratorRequestDto {
    userRequest: UserRequestDto;
    collaboratorRequest: CollaboratorRequestDto;
}