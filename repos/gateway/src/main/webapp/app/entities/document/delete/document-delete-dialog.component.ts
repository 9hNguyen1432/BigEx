import { Component, OnDestroy } from '@angular/core';
import {  Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDocument } from '../document.model';
import { DocumentService } from '../service/document.service';

@Component({
  templateUrl: './document-delete-dialog.component.html',
})
export class DocumentDeleteDialogComponent implements OnDestroy {
  document?: IDocument;


  constructor(protected documentService: DocumentService, protected activeModal: NgbActiveModal, private router: Router) {}
  ngOnDestroy(): void {
    this.router.navigate(['/document', this.document?.employeeId]);
  }

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.documentService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');

    });
  }
}
