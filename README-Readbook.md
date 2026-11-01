# Readbook — branch modernization

Branch built from master, with isolated security and circulation fixes.

## Configuration
Export `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET_BASE64` before starting.
Generate JWT_SECRET_BASE64 with `openssl rand -base64 64`. The application fails to start with a missing or too-short JWT secret. Existing JWTs become invalid after rotation.

**Incident response:** The original DB credentials and JWT key were committed in git history. Rotate/revoke them at their providers; removing literals from the current branch is insufficient.

## Implemented
- Route authorization defaults to deny; login and registration explicitly permitted.
- Active users and current roles are loaded from the database on JWT requests.
- Self-profile edits use the authenticated ID; JSON account IDs cannot target other users.
- Passwords are write-only in JSON responses.
- Each loan references one user with a many-to-one relationship.
- Borrow and both return endpoints delegate to a single transaction service using pessimistic locks on the inventory/book and loan rows. Repeat returns are rejected and return notifications are queued in the same transaction.
- Users can borrow the same title again after returning, because the active-loan lookup ignores completed loans.

## Limitations / release checklist
- Inventory still uses the legacy `book.quantity` model; a full `BookCopy` migration is not implemented.
- `spring.jpa.hibernate.ddl-auto=update` remains for compatibility with existing installations. Replace with tested Flyway migrations before production. Back up real databases before applying schema changes, especially the `borrow.user_id` relationship.
- No Maven build, database integration test, or end-to-end test has been executed in this connector environment. Do not merge/deploy without running tests.
- Add policy-based loan limits, renewal rules, holds FIFO, report filters, audit trails, and API documentation separately.
- Review all endpoints and repositories for additional legacy issues. In particular, authorization of staff account edits needs role-specific hardening.
