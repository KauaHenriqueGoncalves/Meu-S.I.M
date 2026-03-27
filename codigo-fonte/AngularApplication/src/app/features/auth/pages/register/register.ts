import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterStepUser } from "../../components/register-step-user/register-step-user";
import { IconArrowLeft } from "../../../../shared/components/icons/icon-arrow-left.icon";
import { RegisterStepSchool } from "../../components/register-step-school/register-step-school";
import { UserRequest } from '../../models/user-request.model';
import { SchoolRequest } from '../../models/school-request.model';

@Component({
  selector: 'app-register',
  imports: [RegisterStepUser, IconArrowLeft, RegisterStepSchool],
  templateUrl: './register.html',
  styleUrl: './register.sass',
})
export class Register {
  step = 0;

  userData: any = {};
  schoolData: any = {};

  constructor(
    private router: Router 
  ) { }

  backToHome() {
    this.router.navigate(['/']);
  }

  nextStep(data: UserRequest) {
    this.userData = data;
    this.step = 1;
    console.log("userdata: ", this.userData)
  }

  backStep() {
    this.step = 0;
    this.userData = {};
    this.schoolData = {};
  }

  finish(data: SchoolRequest) {
    this.schoolData = data;

    const payload = {
      user: this.userData,
      school: this.schoolData
    };

    console.log('Enviar pro backend:', payload);
  }
}
