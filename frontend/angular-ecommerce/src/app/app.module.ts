import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {ProductService} from './services/product.service';
import {NgOptimizedImage} from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    ProductListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
  ],
  providers: [provideHttpClient(withInterceptorsFromDi()), ProductService],
  bootstrap: [AppComponent]
})
export class AppModule { }
