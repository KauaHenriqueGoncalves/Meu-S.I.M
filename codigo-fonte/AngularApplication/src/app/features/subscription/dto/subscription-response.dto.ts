import { SubscriptionStatus } from "../enum/subscription-status.enum";

export interface SubscriptionResponseDto {
  id: string;
  planName: string;
  startDate: Date;
  endDate: Date;
  status: SubscriptionStatus;
}