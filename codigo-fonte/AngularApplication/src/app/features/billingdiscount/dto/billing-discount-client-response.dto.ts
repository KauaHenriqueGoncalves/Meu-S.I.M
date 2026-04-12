import { Decimal } from 'decimal.js'

export interface BillingDiscountClientResponse {
    months: number,
    discountPercent: Decimal
}