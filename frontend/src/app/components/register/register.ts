import {Component, signal} from '@angular/core';
import {Role} from '@common/role';
import {form, FormField} from '@angular/forms/signals';

interface RegData {
  name: string;
  email: string;
  password: string;
  role: Role;
}

@Component({
  selector: 'app-register',
  imports: [FormField],
  templateUrl: './register.html',
  styleUrl: './register.css',
})


export class Register {
  regModel = signal({
    name: "",
    email: "",
    password: "",
    role: Role.USER
  })

  regForm = form(this.regModel)
}
