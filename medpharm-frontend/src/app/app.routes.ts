import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login.component';
import { RecetasListComponent } from './components/recetas-list.component';
import { RecetaFormComponent } from './components/receta-form.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'recetas', component: RecetasListComponent, canActivate: [authGuard] },
  { path: 'nueva-receta', component: RecetaFormComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'recetas' }
];
