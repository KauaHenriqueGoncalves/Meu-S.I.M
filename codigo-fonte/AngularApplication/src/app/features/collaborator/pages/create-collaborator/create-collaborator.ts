import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CollaboratorApi } from '../../api/collaborator.api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { finalize } from 'rxjs';
import { CreateCollaboratorRequestDto } from '../../dto/create-collaborator-request.dto';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';

@Component({
  selector: 'app-create-collaborator',
  imports: [
    ReactiveFormsModule,
    ArrowLeftSvg
  ],
  templateUrl: './create-collaborator.html',
  styleUrl: './create-collaborator.sass',
})
export class CreateCollaborator implements OnInit {
  collaboratorForm!: FormGroup;
  isSubmitting = false;

  minDate = '1950-01-01';
  maxDate = new Date().toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private collaboratorApi: CollaboratorApi,
    private router: Router,
    private notificationService: NotificationService
  ) { }

  get userForm(): FormGroup {
    return this.collaboratorForm.get('userRequest') as FormGroup;
  }

  get colabForm(): FormGroup {
    return this.collaboratorForm.get('collaboratorRequest') as FormGroup;
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.collaboratorForm = this.fb.group({
      userRequest: this.fb.group({
        username: ['', [
          Validators.required,
          Validators.maxLength(100)
        ]],
        email: ['', [
          Validators.required,
          Validators.email,
          Validators.maxLength(255)
        ]],
        password: ['', [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(20)
        ]],
        cpf: ['', [
          Validators.required,
          cpfValidator()
        ]],
        phoneNumber: ['', [
          Validators.required,
          Validators.maxLength(20)
        ]],
        address: ['', [
          Validators.maxLength(100)
        ]]
      }),
      collaboratorRequest: this.fb.group({
        dateOfBirth: ['', [
          Validators.required
        ]],
        specialty: ['', [
            Validators.required, 
            Validators.minLength(3),
            Validators.maxLength(30)
        ]],
        workload: ['', [
          Validators.required,
          Validators.pattern(/^(\d{1,2})h$/)
        ]]
      })
    });
  }

  goBack(): void {
    this.router.navigate(['/app/collaborators']);
  }

  isFieldInvalid(group: FormGroup, field: string): boolean {
    const control = group.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit(): void {
    if (this.collaboratorForm.invalid) {
      this.collaboratorForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Por favor, preencha todos os campos obrigatórios corretamente.'
      });
      return;
    }

    this.isSubmitting = true;

    const formValue = this.collaboratorForm.value;

    const payload: CreateCollaboratorRequestDto = {
      userRequest: {
        username: formValue.userRequest.username?.trim(),
        email: formValue.userRequest.email?.trim(),
        password: formValue.userRequest.password?.trim(),
        cpf: formValue.userRequest.cpf?.trim(),
        phoneNumber: formValue.userRequest.phoneNumber?.trim(),
        address: formValue.userRequest.address?.trim()
      },
      collaboratorRequest: {
        dateOfBirth: new Date(formValue.collaboratorRequest.dateOfBirth + 'T00:00:00'),
        specialty: formValue.collaboratorRequest.specialty?.trim(),
        workload: formValue.collaboratorRequest.workload?.trim()
      }
    };

    this.collaboratorApi.create(payload)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Colaborador criado com sucesso!'
          });
          this.router.navigate(['/app/collaborators']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao criar colaborador. Tente novamente.'
          });
        }
      });
  }
}
