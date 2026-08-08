# AQVP Team Guidelines

Academic Qualification Verification Platform (AQVP)

Version: 1.0  
Last updated: 2026-07-29

---

## 1. Purpose

This document defines how the AQVP delivery team collaborates, plans, develops, integrates, tests, and releases software. 
It is the working agreement for the team and the reference for onboarding, sprint ceremonies, code reviews, and release management.

These guidelines apply to all team members, contractors, and AI assistants contributing to the codebase.

---

## 2. Project Status

Sprint 1 — the **Identity & Access Module** — has been completed, fully tested, tagged as `v0.1.0-identity`, and merged into the `develop` branch.

- The Identity module **must not be rewritten or duplicated**.
- Bug fixes in the Identity module are allowed **only if approved by the Technical Lead** and tracked in the backlog.
- New modules must integrate with the Identity module through its documented REST API, DTO contracts, JWT-based security, and role/permission model.

---

## 3. Team Roles and Responsibilities

### 3.1 Lovemore Johannes — Technical Lead / DevOps Lead

- Owns project architecture and ensures technical consistency across modules.
- Administers GitHub repositories, branch protection, and access control.
- Reviews all high-risk pull requests and integration points.
- Owns CI/CD pipelines, Docker images, Kubernetes manifests, and release automation.
- Establishes the frontend foundation and cross-cutting technical standards.
- Facilitates sprint planning, release planning, and merge strategy decisions.

### 3.2 Tsakane Sithole — Backend Developer

- Institution Module
- Program Module
- Faculty Module
- Department Module
- Database integration for the above domains

### 3.3 Takunda Mazambani — Backend Developer

- Qualification Module
- Student Records Module
- Qualification Issuance
- Qualification Amendment
- Qualification Revocation

### 3.4 Wonder Mangwendeza — Backend Developer

- Verification Engine (AQVP)
- Verification Requests
- Consent Validation
- QR Verification
- Verification APIs

### 3.5 Memory Chikomo — Backend Developer

- Document Management Module
- PDF Certificate Generation
- QR Code Generation
- Digital Signature Integration
- Document Storage

### 3.6 Tariro Mutunami — QA Engineer / DevOps Engineer

- Audit Module
- Notification Module
  - Email Notifications
  - SMS Notifications
- Automated Testing
- Integration Testing
- Quality Assurance and release sign-off

---

## 4. Communication

- **Daily Stand-up**: 09:00–09:15, Monday to Friday. Each member answers: what was done, what is next, what is blocked.
- **Primary channel**: Project Slack/Teams channel `#aqvp-dev`.
- **Urgent blockers**: Direct message the Technical Lead.
- **Code discussions**: Use GitHub pull request comments; do not rely on informal chat for decisions that affect the codebase.
- **Sprint cadence**: Two-week sprints. Sprint planning on the first Monday; review and retrospective on the last Friday.
- **Documentation updates**: Announced in `#aqvp-dev` and linked in the sprint notes.

---

## 5. Scrum Events

| Event | Frequency | Participants | Purpose |
|---|---|---|---|
| Sprint Planning | Start of sprint | Whole team | Define sprint goal, select backlog items, assign owners |
| Daily Stand-up | Daily | Developers + QA | Synchronise progress and unblock work |
| Backlog Refinement | Mid-sprint | Whole team | Clarify upcoming stories, estimate, split large items |
| Sprint Review | End of sprint | Whole team + stakeholders | Demo working software and collect feedback |
| Sprint Retrospective | End of sprint | Whole team | Inspect process and agree improvements |

Definition of Ready:

- Story has a clear title, description, and acceptance criteria.
- Dependencies are identified.
- Story is estimated and sized to finish within the sprint.

Definition of Done:

- Code is written, reviewed, and merged.
- Unit and integration tests pass with meaningful coverage.
- Static analysis (Checkstyle, SpotBugs) passes.
- Documentation and API examples are updated.
- QA signs off on the story.

---

## 6. Git Workflow

This project uses **GitFlow**.

### 6.1 Main Branches

- `main`: production-ready code. Only release merges and hotfixes are allowed.
- `develop`: integration branch for the current sprint. All completed features merge here.
- `feature/*`: individual user stories or technical tasks.
- `release/*`: release preparation branches cut from `develop`.
- `hotfix/*`: urgent production fixes cut from `main`.

### 6.2 Branch Naming

Pattern: `<type>/<short-description>`

Examples:

- `feature/institution-module`
- `feature/verification-qr`
- `bugfix/document-upload-timeout`
- `hotfix/token-expiry`
- `release/v0.2.0`

Use lowercase letters, numbers, and hyphens. Do not use underscores or special characters.

### 6.3 Feature Branch Lifecycle

1. Create `feature/<name>` from the latest `develop`.
2. Make focused, atomic commits with clear messages.
3. Keep the branch short-lived; rebase onto `develop` if it drifts.
4. Open a pull request when the story meets Definition of Done.
5. Merge only after review, CI success, and QA approval.

---

## 7. Pull Requests

- One pull request per story or defect.
- PR title format: `[AQVP-XXX] Short description`.
- Description must include:
  - What changed and why
  - How to test
  - Link to the related issue/story
  - Any breaking changes
- All CI checks must pass before merge.
- Address all review comments before requesting re-review.
- Squash or merge commit may be used depending on the merge strategy for the sprint; default is **merge commit** to preserve history.

---

## 8. Code Reviews

### 8.1 Reviewers

- Every PR requires at least one reviewer.
- Backend changes must be reviewed by another backend developer or the Technical Lead.
- DevOps, security, or architectural changes must be reviewed by the Technical Lead.
- QA must review test coverage and acceptance criteria.

### 8.2 Review Checklist

- Does the code follow the existing architecture and patterns?
- Are there tests for new behaviour?
- Are there duplicated concepts or functions that already exist elsewhere?
- Is input validated and are exceptions handled?
- Does the change respect the Identity module boundaries?
- Are secrets, credentials, or environment-specific values excluded?

### 8.3 Review Etiquette

- Be specific and kind.
- Approve only when confident.
- If requesting changes, explain the impact and offer alternatives.

---

## 9. Merge Strategy

- Default: **merge commit** from `feature/*` into `develop`.
- Use **squash merge** only for cleanup branches or trivial fixes, with the agreement of the reviewer.
- Never force-push to `main` or `develop`.
- Rebase feature branches onto `develop` before merging to keep history linear where possible.
- The Technical Lead has merge rights to `main` and `release/*` branches.

---

## 10. CI/CD Workflow

### 10.1 Continuous Integration

Every push and pull request triggers:

1. Maven compile
2. Unit and integration tests
3. Checkstyle static analysis
4. SpotBugs bug pattern analysis
5. Test coverage report (Jacoco)

### 10.2 Continuous Delivery

- Docker images are built for the `aqvp-identity-service` and future services.
- Image tags follow semantic versions and commit SHAs.
- Helm/Kubernetes manifests are stored in the infrastructure repository.
- Deployments to dev and staging are automatic after successful CI on `develop`.
- Production releases require manual approval from the Technical Lead.

### 10.3 Local Development

- Use the provided `application-local.yml` and `local-h2` Maven profile when a PostgreSQL instance is not available.
- Use `docker-compose.test.yml` for integration testing when Docker is available.
- Follow the README and testing report in each module for setup steps.

---

## 11. Testing Responsibilities

### 11.1 Developer Responsibilities

- Write unit tests for services, mappers, validators, and controllers.
- Write integration tests for repository and security layers.
- Ensure tests pass locally before pushing.
- Maintain or improve code coverage.

### 11.2 QA Responsibilities

- Write and maintain automated integration and end-to-end tests.
- Validate acceptance criteria for each story.
- Perform exploratory testing where needed.
- Own the audit, notification, and cross-cutting quality modules.
- Sign off on releases.

### 11.3 Testing Standards

- Minimum unit test coverage target: 80%.
- All security and authentication paths must have tests.
- Testcontainers-based integration tests must run in Docker-capable environments.
- No failing tests may be merged.

---

## 12. Documentation Responsibilities

- Each module owner keeps the module README current with build, run, and test instructions.
- API changes require updates to:
  - OpenAPI/Swagger annotations and examples
  - Postman collections under `src/test/resources/testing/postman`
  - Request/response samples under `src/test/resources/testing/requests-responses`
- Architectural or integration decisions are recorded in `docs/adr/`.
- This team guidelines document is owned by the Technical Lead and updated by consensus.

---

## 13. AI Usage Policy

- AI coding assistants may be used for drafting code, tests, documentation, and debugging.
- AI-generated output must be reviewed, understood, and validated by a human before commit.
- Do not commit generated secrets, credentials, hard-coded tokens, or environment-specific values.
- AI must not be used to bypass code review, tests, or security controls.
- Do not use AI to rewrite the completed Identity module; all changes to Identity must be reviewed by the Technical Lead.
- Team members are accountable for the quality and correctness of any AI-assisted code they submit.

---

## 14. Sprint Workflow

1. **Sprint Planning**: Select stories, assign owners, clarify the sprint goal.
2. **Story Development**: Create feature branch, implement, test, and document.
3. **Code Review**: Open PR, address feedback, merge to `develop`.
4. **Integration**: Pull `develop` regularly and resolve conflicts early.
5. **QA Validation**: QA verifies stories and runs regression tests.
6. **Sprint Review**: Demonstrate completed work.
7. **Sprint Retrospective**: Agree process improvements.
8. **Release Preparation**: Technical Lead cuts `release/*` branch when `develop` is stable.

### Identity Module Constraint

- During every sprint, verify that new modules consume the Identity API rather than reimplementing authentication, user management, role/permission logic, or JWT handling.
- If a story appears to overlap with Identity, raise it in refinement or stand-up.

---

## 15. Conflict Resolution

1. **Technical disagreement**: Discuss in the PR or a brief huddle; if unresolved, escalate to the Technical Lead.
2. **Scope creep**: Revisit the story and sprint goal with the team; escalate to the Technical Lead if it threatens the sprint.
3. **Integration conflicts**: The developer owning the later branch is responsible for rebasing and resolving conflicts.
4. **Process disagreement**: Raise in the retrospective; the team decides by consensus.
5. **Urgent production issues**: Technical Lead may override normal process, documenting the decision afterwards.

---

## 16. Release Strategy

### 16.1 Versioning

Semantic Versioning: `MAJOR.MINOR.PATCH`

- MAJOR: incompatible API changes
- MINOR: new functionality, backward compatible
- PATCH: bug fixes

### 16.2 Release Process

1. Technical Lead creates `release/vX.Y.Z` from `develop`.
2. QA performs regression and release testing.
3. Fix release-specific bugs on the release branch.
4. Merge `release/vX.Y.Z` into both `main` and `develop`.
5. Tag `main` with `vX.Y.Z`.
6. Build Docker image from the release tag.
7. Deploy to staging, then production after approval.

### 16.3 Current Releases

- `v0.1.0-identity` — Identity & Access Module (released)

---

## 17. References

- `README.md` — project overview and setup
- `docs/adr/` — architecture decision records
- `aqvp-identity-service/src/test/resources/testing/report/identity-testing-report.md` — Sprint 1 test report
- The Swagger UI (local): `http://localhost:8081/swagger-ui.html`

---

*These guidelines are a living document. Propose changes through a pull request or retrospective action item.*
