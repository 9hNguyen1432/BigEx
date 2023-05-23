package com.leap.training.gateway.web.rest;

import com.leap.training.gateway.domain.Document;
import com.leap.training.gateway.repository.DocumentRepository;
import com.leap.training.gateway.security.AuthoritiesConstants;
import com.leap.training.gateway.service.DocumentService;
import com.leap.training.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.leap.training.gateway.domain.Document}.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

    private static final String ENTITY_NAME = "documentDocument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocumentService documentService;

    private final DocumentRepository documentRepository;

    public DocumentResource(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    public String currentUserNameSimple(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal.getName();
    }

    /**
     * {@code POST  /documents} : Create a new document.
     *
     * @param document the document to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new document, or with status {@code 400 (Bad Request)} if the document has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PostMapping("/documents")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Document> createDocument(@RequestBody Document document) throws URISyntaxException {
        log.debug("REST request to save Document : {}", document);
        if (document.getId() != null) {
            throw new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Document result = documentService.save(document);
        return ResponseEntity
            .created(new URI("/api/documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /documents/:id} : Updates an existing document.
     *
     * @param id the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/documents/{emid}/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Document> updateDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Document document
    ) throws URISyntaxException {
        log.debug("REST request to update Document : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Document result = documentService.save(document);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /documents/:id} : Partial updates given fields of an existing document, field will ignore if it is null
     *
     * @param id the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 404 (Not Found)} if the document is not found,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/documents/{emid}/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Document> partialUpdateDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Document document
    ) throws URISyntaxException {
        log.debug("REST request to partial update Document partially : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Document> result = documentService.partialUpdate(document);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId().toString())
        );
    }

    /**
     * {@code GET  /documents} : get all the documents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of documents in body.
     */
    @GetMapping("/documents/{emid}")
    public ResponseEntity<List<Document>> getAllDocuments(Pageable pageable,@PathVariable Long emid) {
        log.debug("REST request to get a page of Documents");
        Page<Document> page = documentService.findAllByEmployeeId(pageable, emid);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /documents/:id} : get the "id" document.
     *
     * @param id the id of the document to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the document, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/documents/{emid}/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long emid, @PathVariable Long id) {
        log.debug("REST request to get Document : {}", id);
        Optional<Document> document = documentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(document);
    }

    /**
     * {@code DELETE  /documents/:id} : delete the "id" document.
     *
     * @param id the id of the document to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/documents/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
