# Readbook — modernization branch

This branch is isolated from master. It tightens JWT handling, authorization and circulation consistency.

## Required configuration
Set DB_URL, DB_USERNAME, DB_PASSWORD and JWT_SECRET_BASE64 in the runtime environment.
Generate a secret with `openssl rand -base64 64`. Rotate the credentials and JWT key previously present in git history; removing hard-coded values does not invalidate leaked secrets.

## Circulation
The new `CirculationService` uses database row locks and a transaction for borrow/return; repeat returns are rejected. Old endpoints must delegate to this service to obtain those protections.

## Pending
Individual BookCopy records, PostgreSQL migrations, loan policies/renewals, holds, audit logging, API docs, integration tests and replacing deprecated dependencies remain future work. Do not deploy this branch until end-to-end tests pass.
