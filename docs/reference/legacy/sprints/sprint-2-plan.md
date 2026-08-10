# AQVP Sprint 2 Plan

Academic Qualification Verification Platform (AQVP)

Sprint Goal: Develop all remaining business modules while integrating with the completed Identity module.

Sprint: 2  
Duration: 2 weeks  
Prepared by: Scrum Master  
Last updated: 2026-07-29

---

## 1. Current Status

- Sprint 1 completed successfully.
- Identity & Access Module implemented, tested, tagged `v0.1.0-identity`, and merged into `develop`.
- Authentication APIs, JWT security, role/permission model, and Swagger documentation are operational and available.
- Sprint 2 will **not** include any Identity module development or changes, except approved bug fixes escalated to the Technical Lead.

---

## 2. Sprint Goal

Deliver the core business modules and frontend foundation required for qualification issuance, verification, document generation, audit, and notifications — all integrated with the Identity module via its documented APIs.

---

## 3. Sprint Objectives

The following modules must reach a working, testable, and integrated state by the end of Sprint 2:

1. Institution Module
2. Qualification Module
3. Verification Module
4. Document Module
5. Audit Module
6. Notification Module
7. React Frontend Foundation

---

## 4. Team Responsibilities

| Team Member | Role | Sprint 2 Responsibilities |
|---|---|---|
| Lovemore Johannes | Technical Lead / DevOps Lead | Frontend Foundation; Project Integration; CI/CD; Architecture; Code Reviews |
| Tsakane Sithole | Backend Developer | Institution Module |
| Takunda Mazambani | Backend Developer | Qualification Module |
| Wonder Mangwendeza | Backend Developer | Verification Engine |
| Memory Chikomo | Backend Developer | Document Module |
| Tariro Mutunami | QA Engineer / DevOps Engineer | Audit Module; Notification Module; Testing |

---

## 5. Sprint Backlog

| ID | Story | Owner | Priority | Estimate |
|---|---|---|---|---|
| S2-001 | Institution domain model, repository, service, and REST API | Tsakane Sithole | High | 8 |
| S2-002 | Institution CRUD endpoints with validation and DTOs | Tsakane Sithole | High | 5 |
| S2-003 | Qualification domain model, repository, service, and REST API | Takunda Mazambani | High | 8 |
| S2-004 | Qualification issuance workflow and status lifecycle | Takunda Mazambani | High | 8 |
| S2-005 | Student Records data model and CRUD operations | Takunda Mazambani | High | 5 |
| S2-006 | Verification request intake and status tracking | Wonder Mangwendeza | High | 8 |
| S2-007 | Consent validation and QR verification generation | Wonder Mangwendeza | High | 8 |
| S2-008 | Verification public APIs and engine scoring | Wonder Mangwendeza | High | 8 |
| S2-009 | Document storage abstraction and metadata management | Memory Chikomo | High | 5 |
| S2-010 | PDF certificate generation service | Memory Chikomo | High | 8 |
| S2-011 | QR code and digital signature integration for certificates | Memory Chikomo | High | 8 |
| S2-012 | Audit event model and audit trail service | Tariro Mutunami | High | 5 |
| S2-013 | Audit endpoints and searchable audit log | Tariro Mutunami | Medium | 5 |
| S2-014 | Notification service abstraction (email and SMS) | Tariro Mutunami | High | 8 |
| S2-015 | React frontend foundation: project setup, routing, and auth integration | Lovemore Johannes | High | 8 |
| S2-016 | CI/CD pipeline expansion for new modules | Lovemore Johannes | High | 5 |
| S2-017 | Cross-module integration and end-to-end testing | Lovemore Johannes | High | 5 |

---

## 6. Acceptance Criteria

### Institution Module
- Institutions can be created, updated, deactivated, and retrieved.
- Institution code is unique and validated.
- Endpoints are secured and require appropriate permissions.
- Swagger examples are documented.

### Qualification Module
- Qualifications can be created and linked to institutions/programs.
- Issuance, amendment, and revocation update status correctly.
- Student records can be created and associated with issued qualifications.
- Business rules enforce who can perform each action.

### Verification Module
- Verification requests can be submitted and tracked.
- Consent validation prevents unauthorized lookups.
- QR code generation produces verifiable references.
- Public verification API returns consistent responses.

### Document Module
- Documents can be uploaded, stored, and retrieved.
- PDF certificate generation uses qualification data.
- QR code and digital signature are embedded in certificates.
- Generated documents can be validated.

### Audit Module
- Audit events are captured for key actions.
- Audit log is searchable by actor, action, resource, and timestamp.
- Events are immutable after creation.

### Notification Module
- Email and SMS notifications can be triggered through a unified service.
- Notification templates support common events.
- Failed notifications are logged for retry.

### React Frontend Foundation
- React project bootstrapped with routing.
- Authentication flow integrates with Identity JWT endpoints.
- Protected routes and role-based access are implemented.
- Shared layout and navigation are in place.

---

## 7. Definition of Ready

A backlog item is ready for sprint work when:

- The story has a clear title, description, and acceptance criteria.
- Dependencies on the Identity module or other modules are identified.
- The owner understands the integration contract (DTOs, endpoints, permissions).
- The story is estimated and fits within the sprint.
- UI/UX or API design decisions are documented or agreed upon.

---

## 8. Definition of Done

A story is done when:

- Code is implemented and self-reviewed.
- Unit tests are written and pass.
- Integration tests cover database and security layers where applicable.
- Static analysis (Checkstyle, SpotBugs) passes with no new violations.
- Code is reviewed and approved by at least one reviewer.
- API documentation (OpenAPI/Swagger) is updated.
- QA validates the acceptance criteria.
- No Identity module duplication or overlap has been introduced.

---

## 9. Risk Register

| ID | Risk | Likelihood | Impact | Mitigation | Owner |
|---|---|---|---|---|---|
| R01 | Tight coupling to Identity module APIs causes integration delays | Medium | High | Consume Identity APIs only; review contracts early; raise API gaps to Technical Lead | Lovemore Johannes |
| R02 | Module owners create overlapping domain concepts (e.g., qualification status vs document status) | Medium | Medium | Maintain shared domain glossary; daily stand-up synchronization; architecture review | Lovemore Johannes |
| R03 | Frontend auth integration blocks UI development | Medium | High | Build auth integration first; use Swagger examples as contract | Lovemore Johannes |
| R04 | Document generation performance degrades with large payloads | Low | High | Implement async processing stub; review PDF library choice early | Memory Chikomo |
| R05 | Notification provider configuration unavailable in local/dev environments | Medium | Medium | Provide mock notification profile; abstract provider behind interface | Tariro Mutunami |
| R06 | Sprint scope increases due to unplanned Identity bug fixes | Low | Medium | Log Identity bugs separately; only fix with Technical Lead approval | Scrum Master |
| R07 | QA bandwidth constrained by parallel module delivery | Medium | High | Stagger final QA checkpoints; automate regression where possible | Tariro Mutunami |

---

## 10. Communication Plan

| Channel | Purpose | Audience | Cadence |
|---|---|---|---|
| `#aqvp-dev` | Daily updates, blockers, links to PRs/docs | Whole team | Continuous |
| Daily Stand-up | Sync progress and unblock work | Whole team | Daily, 09:00–09:15 |
| GitHub PR comments | Technical decisions and review feedback | Authors and reviewers | As needed |
| Sprint Review | Demo and stakeholder feedback | Whole team + stakeholders | Last Friday of sprint |
| Sprint Retrospective | Process improvements | Whole team | Last Friday of sprint |
| Architecture huddle | Integration and design alignment | Technical Lead + module owners | As needed |

---

## 11. Sprint Timeline

### Week 1

| Day | Focus | Key Activities |
|---|---|---|
| Monday | Sprint Planning + Kick-off | Finalize backlog; confirm API contracts; set up feature branches |
| Tuesday | Backend Foundation | Domain models, repositories, and Flyway migrations for Institution, Qualification, Verification, Document, Audit, Notification |
| Wednesday | Core Services | Service layer implementation; DTOs; mappers; integration with Identity permissions |
| Thursday | REST APIs + Frontend Scaffold | Controllers; OpenAPI examples; React project setup; JWT auth flow |
| Friday | Mid-sprint Review + Refinement | Demo progress; refine remaining stories; address blockers |

### Week 2

| Day | Focus | Key Activities |
|---|---|---|
| Monday | Advanced Features | Qualification issuance, verification engine, PDF generation, audit logging, notification dispatch |
| Tuesday | Frontend Integration | Module screens, protected routes, API consumption |
| Wednesday | Integration + QA Start | Cross-module integration tests; QA validation begins |
| Thursday | Bug Fixes + Documentation | Fix defects; update README, Swagger, Postman, and module docs |
| Friday | Sprint Review + Retrospective | Demo to stakeholders; retrospective; plan Sprint 3 |

---

## 12. Pull Request Rules

- One pull request per story or bug fix.
- PR title format: `[S2-XXX] Short description`.
- PR description must include:
  - Summary of changes
  - How to test
  - Link to story
  - Breaking changes, if any
- All CI checks must pass before merge.
- At least one reviewer must approve.
- Backend and architectural changes require Technical Lead review.
- QA must review test coverage and acceptance criteria.
- Re-request review after pushing changes.

---

## 13. Merge Strategy

- Feature branches merge into `develop` using **merge commit** by default.
- Use **squash merge** only for small cleanup branches with reviewer agreement.
- Rebase feature branches onto `develop` before merging if the branch has drifted.
- Never force-push to `main` or `develop`.
- The Technical Lead owns merges to `main` and `release/*` branches.

---

## 14. Git Branches

| Branch | Purpose | Cut From |
|---|---|---|
| `main` | Production-ready code | — |
| `develop` | Current sprint integration | — |
| `feature/S2-XXX-short-name` | Sprint stories | `develop` |
| `release/v0.2.0` | Release preparation | `develop` |
| `hotfix/*` | Urgent production fixes | `main` |

Branch naming examples:

- `feature/S2-001-institution-module`
- `feature/S2-010-pdf-certificate-generation`
- `bugfix/S2-003-qualification-status-update`

---

## 15. Testing Plan

### Unit Testing
- Each module service and mapper has unit tests with mocked dependencies.
- Controllers are tested with MockMvc and security context.

### Integration Testing
- Repository tests use the H2 test profile.
- Module-to-Identity integration tests use `@SpringBootTest` with the test security configuration.
- Audit and Notification modules are tested with stub providers.

### End-to-End Testing
- QA leads integration tests across Institution → Qualification → Document → Verification flow.
- Frontend authentication flow is verified against the running Identity service.

### Static Analysis
- Checkstyle and SpotBugs run on every build.
- No new violations are introduced.

### Test Environments
- Local: `local-h2` Maven profile
- CI: Docker-based build with H2 for unit/integration tests
- Staging: Docker Compose / Kubernetes deployment with PostgreSQL

---

## 16. Release Plan

Sprint 2 targets a releasable increment on `develop`.

| Milestone | Target Date | Deliverable |
|---|---|---|
| Feature complete | End of Week 1 | Domain models, repositories, service layer |
| API complete | Mid Week 2 | All module REST endpoints documented |
| Integration complete | End of Week 2 | Cross-module flows tested |
| Release candidate | End of Week 2 | `develop` stable; tag candidate `v0.2.0-rc1` |
| Official release | Sprint 3 start | Cut `release/v0.2.0`, merge to `main`, tag `v0.2.0` |

Release criteria:

- All Sprint 2 stories meet Definition of Done.
- `develop` branch passes CI/CD with no failures.
- QA signs off on regression and integration testing.
- Docker images are built and deployed to staging.

---

## 17. Retrospective Template

Use this template during the Sprint 2 retrospective.

### What went well?
- List practices, tools, or decisions that worked.
- Celebrate completed work.

### What could be improved?
- Identify blockers, slowdowns, or quality issues.
- Note any scope or communication gaps.

### What will we change in Sprint 3?
- Define one to three actionable improvements.
- Assign owners and a review checkpoint.

### Metrics
- Stories committed: ___
- Stories completed: ___
- Bugs found: ___
- Bugs fixed: ___
- Test coverage: ___%
- CI pass rate: ___%

### Action Items
| Action | Owner | Due Date |
|---|---|---|
| | | |
| | | |

---

## 18. Constraints and Assumptions

- The Identity module is complete and available as a shared dependency.
- New modules must use Identity JWT tokens for authentication and Identity permissions for authorization.
- No module may reimplement Identity concerns (users, roles, permissions, JWT).
- Local development can use the H2 profile until a shared PostgreSQL instance is provisioned.

---

*This plan is a living document. Adjustments are made through team agreement and tracked in the sprint backlog or retrospective action items.*
