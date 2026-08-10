import { DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../../shared/components/spinner-to-button/spinner-to-button';
import { EditSvg } from '../../../../../shared/components/svg/edit.svg';
import { PlusSvg } from '../../../../../shared/components/svg/plus.svg';
import { TrashSvg } from '../../../../../shared/components/svg/trash.svg';
import { NoEmojiDirective } from '../../../../../shared/directives/no-emoji.directive';
import { NumbersOnlyDirective } from '../../../../../shared/directives/numbers-only.directive';
import { SchoolPlanResponseDto } from '../../dto/school-plan-response.dto';
import { AdminSchoolPlanApi } from '../../api/admin-school-plan-api';
import { NotificationService } from '../../../../../core/services/notification/notification.service';
import { timeout, catchError, throwError, finalize } from 'rxjs';
import { CreateSchoolPlanRequestDto } from '../../dto/create-school-plan-request.dto';
import { UpdateSchoolPlanRequestDto } from '../../dto/update-school-plan-request.dto';

@Component({
  selector: 'app-admin-school-plan',
  imports: [
    ReactiveFormsModule,
    DecimalPipe,
    SpinnerToButton,
    PlusSvg,
    EditSvg,
    TrashSvg,
    NoEmojiDirective,
    NumbersOnlyDirective
  ],
  templateUrl: './admin-school-plan.html',
  styleUrl: './admin-school-plan.sass',
})
export class AdminSchoolPlan implements OnInit {
  plans: SchoolPlanResponseDto[] = [];
  isLoading = true;
  isSubmitting = false;

  isModalOpen = false;
  isDeleteModalOpen = false;

  private formValues: any;

  planForm!: FormGroup;
  editingId: string | null = null;
  deletingId: string | null = null;

  constructor(
    private schoolPlanApi: AdminSchoolPlanApi,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadPlans();
  }

  get isFormChanged(): boolean {
    if (!this.formValues) return false;
    return JSON.stringify(this.planForm.getRawValue()) !== JSON.stringify(this.formValues);
  }

  initForm(): void {
    this.planForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50)
      ]],
      monthlyPrice: [null, [
        Validators.required,
        Validators.min(0.1)
      ]],
      maxStudents: [null, [
        Validators.required,
        Validators.min(1)
      ]],
      maxCollaborators: [null, [
        Validators.required,
        Validators.min(1)
      ]],
      maxLegalGuardian: [null, [
        Validators.required,
        Validators.min(1)
      ]],
      maxSchoolAdmin: [null, [
        Validators.required,
        Validators.min(1)
      ]],
      isActive: [true]
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.planForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadPlans(): void {
    this.isLoading = true;
    this.cdr.detectChanges();

    this.schoolPlanApi.findAll()
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
          this.plans = response;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado ao carregar planos.'
          });
        }
      });
  }

  openModal(plan?: SchoolPlanResponseDto): void {
    if (plan) {
      this.editingId = plan.id;
      this.planForm.patchValue({
        name: plan.name,
        monthlyPrice: plan.monthlyPrice,
        maxStudents: plan.maxStudents,
        maxCollaborators: plan.maxCollaborators,
        maxLegalGuardian: plan.maxLegalGuardian,
        maxSchoolAdmin: plan.maxSchoolAdmin,
        isActive: plan.isActive
      });
    }
    else {
      this.editingId = null;
      this.planForm.reset({ isActive: true });
    }
    this.formValues = this.planForm.getRawValue();
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.editingId = null;
    this.planForm.reset();
    this.formValues = null;
  }

  onSavePlan(): void {
    if (this.planForm.invalid) {
      this.planForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formValue = this.planForm.value;

    const request$ = this.editingId
      ? this.schoolPlanApi.update(this.editingId, formValue as UpdateSchoolPlanRequestDto)
      : this.schoolPlanApi.create({
        name: formValue.name.trim(),
        monthlyPrice: formValue.monthlyPrice,
        maxStudents: formValue.maxStudents,
        maxCollaborators: formValue.maxCollaborators,
        maxLegalGuardian: formValue.maxLegalGuardian,
        maxSchoolAdmin: formValue.maxSchoolAdmin
      } as CreateSchoolPlanRequestDto);

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
        this.loadPlans();
        this.notificationService.notify({
          type: 'success',
          text: this.editingId ? 'Plano atualizado com sucesso!' : 'Plano criado com sucesso.'
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
    this.schoolPlanApi.deleteById(this.deletingId)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadPlans();
          this.notificationService.notify({
            type: 'success',
            text: 'Plano excluído com sucesso.'
          });
        },
        error: (err) => {
          let message: string = err.error?.message || 'Erro ao excluir o plano.';
          if (err.status === 409) {
            message = 'Não é possível excluir o plano pois ele está associado a uma licença da escola.';
          }
          this.notificationService.notify({
            type: 'error',
            text: message
          });
        }
      });
  }
}
