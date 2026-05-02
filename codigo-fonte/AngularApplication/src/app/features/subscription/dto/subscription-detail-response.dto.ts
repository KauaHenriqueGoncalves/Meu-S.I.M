import { PaymentStatus } from "../../subscriptionpayment/enum/payment-status.enum";
import { SubscriptionStatus } from "../enum/subscription-status.enum";

export interface SchoolSubscriptionDetailResponse {
  id: string;
  months: number;
  planName: string;
  planPrice: number; // ou Decimal, dependendo de como você mapeia
  maxStudents: number;
  maxCollaborators: number;
  maxLegalGuardian: number;
  maxSchoolAdmin: number;
  startDate: Date;
  endDate: Date;
  subscriptionStatus: SubscriptionStatus;
  discountAmount: number;
  originalAmount: number;
  amount: number;
  paymentMethod: string | null;
  installments: number | null;
  orderId: string | null;
  paymentType: string | null;
  paidAt: Date | null;
  paymentStatus: PaymentStatus;
  providerPaymentId: string;
}