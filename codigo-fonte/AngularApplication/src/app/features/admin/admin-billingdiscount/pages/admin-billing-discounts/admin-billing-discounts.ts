import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../../shared/components/spinner-to-button/spinner-to-button';
import { EditSvg } from '../../../../../shared/components/svg/edit.svg';
import { PlusSvg } from '../../../../../shared/components/svg/plus.svg';
import { TrashSvg } from '../../../../../shared/components/svg/trash.svg';
import { NumbersOnlyDirective } from '../../../../../shared/directives/numbers-only.directive';
import { BillingDiscountResponseDto } from '../../dto/billing-discount-response.dto';
import { AdminBillingdiscountApi } from '../../api/admin-billingdiscount-api';
import { NotificationService } from '../../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { BillingDiscountRequestDto } from '../../dto/billing-discount-request.dto';

@Component({
  selector: 'app-admin-billing-discounts',
  imports: [
    ReactiveFormsModule,
    SpinnerToButton,
    PlusSvg,
    EditSvg,
    TrashSvg,
    NumbersOnlyDirective
  ],
  templateUrl: './admin-billing-discounts.html',
  styleUrl: './admin-billing-discounts.sass',
})
export class AdminBillingDiscounts implements OnInit {
  discounts: BillingDiscountResponseDto[] = [];
  isLoading = true;
  isSubmitting = false;

  isModalOpen = false;
  isDeleteModalOpen = false;

  private formValues: any;

  discountForm!: FormGroup;
  editingId: string | null = null;
  deletingId: string | null = null;

  constructor(
    private billingDiscountApi: AdminBillingdiscountApi,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadDiscounts();
  }

  get isFormChanged(): boolean {
    if (!this.formValues) return false;
    return JSON.stringify(this.discountForm.getRawValue()) !== JSON.stringify(this.formValues);
  }

  initForm(): void {
    this.discountForm = this.fb.group({
      months: [null, [
        Validators.required,
        Validators.min(1),
        Validators.max(60)
      ]],
      discountPercent: [null, [
        Validators.required,
        Validators.min(1),
        Validators.max(100)
      ]]
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.discountForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadDiscounts(): void {
    this.isLoading = true;
    this.cdr.detectChanges();

    this.billingDiscountApi.findAll()
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response: any) => {
          this.discounts = response;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado ao carregar descontos.'
          });
        }
      });
  }

  openModal(discount?: BillingDiscountResponseDto): void {
    if (discount) {
      this.editingId = discount.id;
      this.discountForm.patchValue({
        months: discount.months,
        discountPercent: discount.discountPercent
      });
    }
    else {
      this.editingId = null;
      this.discountForm.reset();
    }
    this.formValues = this.discountForm.getRawValue();
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.editingId = null;
    this.discountForm.reset();
    this.formValues = null;
  }

  onSaveDiscount(): void {
    if (this.discountForm.invalid) {
      this.discountForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const requestData: BillingDiscountRequestDto = {
      months: this.discountForm.value.months,
      discountPercent: this.discountForm.value.discountPercent
    };

    const request$ = this.editingId
      ? this.billingDiscountApi.update(this.editingId, requestData)
      : this.billingDiscountApi.create(requestData);

    request$.pipe(
      timeout(10000),
      finalize(() => {
        this.isSubmitting = false;
        this.formValues = null;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: () => {
        this.closeModal();
        this.loadDiscounts();
        this.notificationService.notify({
          type: 'success',
          text: this.editingId ? 'Desconto atualizado com sucesso!' : 'Desconto criado com sucesso.'
        });
      },
      error: (err) => {
        this.notificationService.notify({
          type: 'error',
          text: err.error?.message || 'Erro inesperado.'
        });
      }
    });
  }

  openDeleteModal(id: string): void {
    this.deletingId = id;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
    this.deletingId = null;
  }

  confirmDelete(): void {
    if (!this.deletingId) return;

    this.isSubmitting = true;
    this.billingDiscountApi.deleteById(this.deletingId)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadDiscounts();
          this.notificationService.notify({
            type: 'success',
            text: 'Desconto excluído com sucesso.'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir o desconto.'
          });
        }
      });
  }
}
