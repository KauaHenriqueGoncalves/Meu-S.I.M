import { Decimal } from "decimal.js";

export interface SubscriptionCheckoutResponseDto {
    title: string,
    planPrice: Decimal,
    months: number,
    initPoint: string,
    preferenceId: string
}
