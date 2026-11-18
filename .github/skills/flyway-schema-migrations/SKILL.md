---
name: flyway-schema-migrations
description: "Author and review PostgreSQL Flyway migrations with safe schema evolution and database-backed validation."
---

# Manage PostgreSQL Flyway Migrations

Use this skill when adding or reviewing Flyway SQL migrations, changing persistent data structures, or coordinating database schema changes with JPA entities and application code.

## Scope

- Follow the `data` module's Flyway configuration and migration location, currently `data/src/main/resources/db/migration/`.
- Treat committed versioned migrations as deployment history. Add a new migration for an already released or applied change; do not silently rewrite history.
- Keep PostgreSQL as the database contract and check the project's configured database and Flyway versions before using version-specific SQL or features.
- Keep schema changes aligned with JPA entities, repositories, mappings, and application behavior.
- Do not run migrations against a shared or production database without explicit approval.

## Required workflow

### 1. Establish the schema and rollout contract

Before editing:

- Read the affected entity mappings, repositories, existing migration history, database configuration, tests, and deployment workflow.
- Locate the highest existing migration version and follow the repository's `V<number>__description.sql` naming convention.
- Confirm supported PostgreSQL versions and whether the change must work with existing rows or rolling application deployments.
- Determine whether the change affects nullability, defaults, keys, indexes, constraints, data retention, or application compatibility.
- Ask a focused question when the required data transformation, production rollout order, downtime tolerance, or backward-compatibility expectation is unknown.

### 2. Design a safe, forward-only migration

- Add a new, monotonically increasing versioned migration; coordinate version assignment when concurrent changes may use the same next version.
- Keep each migration focused, deterministic, and explicit about the tables and data it changes.
- Preserve existing data unless deletion or transformation is a stated requirement. Explain data-loss, lock, availability, and rollback implications before changes with those effects.
- Prefer expand-and-contract steps for changes that must coexist with old and new application versions: add compatible schema first, migrate or backfill data, switch application use, then remove obsolete schema in a separately approved follow-up.
- Make backfills bounded and safe for the actual data volume. Consider lock duration, transaction behavior, index creation impact, and retry or interruption behavior.
- Use database constraints to enforce required invariants when compatible with existing data and application behavior.
- Avoid broad destructive DDL, implicit casts, and `IF EXISTS` or `IF NOT EXISTS` clauses that could hide unexpected schema drift.
- Do not put credentials, personal data, environment-specific values, or production records in migrations.

### 3. Keep application mappings consistent

- Compare changed tables and columns with entity annotations, relationships, converters, and repository queries.
- Check SQL and Java types, nullability, lengths, precision, defaults, generated identifiers, indexes, uniqueness, and foreign keys.
- Update application code and migration together when a schema change requires it; do not rely on Hibernate auto-DDL to create production schema changes.
- Keep existing naming conventions, such as snake_case database columns and the current PostgreSQL UUID strategy, unless a documented migration requires a change.
- Do not change entity behavior, schema constraints, or migration semantics merely for stylistic consistency.

### 4. Test the migration path

Use the existing `data` module's PostgreSQL Testcontainers setup and Flyway startup behavior where available. Tests should verify the migration path relevant to the change:

- a fresh database migrates successfully through all versioned scripts
- an upgrade from the prior supported schema succeeds when existing installations or data are in scope
- representative pre-existing rows retain the intended values after alterations or backfills
- new constraints, defaults, indexes, and relationships behave as intended
- JPA mappings and repository operations work against the migrated schema

Use synthetic test data only. Do not connect test commands to production or shared databases. If an upgrade path or PostgreSQL version cannot be exercised locally, state the limitation rather than assuming success.

### 5. Validate and review

Run the narrowest relevant tests with the Gradle wrapper. For changes to the `data` module, a typical check is:

```bash
./gradlew :data:test
```

Run additional module or application tests when changed mappings or shared data behavior require them. Do not run a migration against an external database as a substitute for isolated validation.

Before finalizing:

- reread the complete migration and every changed mapping
- inspect the diff for accidental edits to existing migration history or unrelated schema
- confirm migration order, SQL syntax, data-preservation behavior, and expected constraints
- identify operations with table-lock, rewrite, long-running backfill, or deployment-order risk
- report the exact validation command, database version or test environment, results, and checks not run

## Safety and decision rules

- Treat applied versioned migrations as immutable; create a forward migration to correct a released change.
- Require explicit approval for data deletion, irreversible transformations, destructive schema changes, or migrations on live environments.
- Do not invent database versions, production data shapes, table sizes, downtime windows, or rollback guarantees.
- Prefer a tested forward repair over editing migration history or manually changing a live schema.
- Distinguish source-level SQL review from a migration that was actually executed against PostgreSQL.
- Report migration risks and unavailable environment-dependent checks clearly.

## Prohibited patterns

- Do not rewrite or renumber an applied migration.
- Do not drop or truncate data, tables, columns, or constraints without an explicit requirement and approval.
- Do not use ORM schema generation as the only record of a production schema change.
- Do not hide schema drift with blanket conditional DDL.
- Do not use production data in fixtures or expose secrets in migration scripts or logs.
- Do not claim a migration is valid based only on reading SQL or compiling Java.

## Expected output

Report:

1. the migration version, affected tables, and schema/application contract
2. data-preservation, compatibility, rollout, and operational risks
3. corresponding entity or repository changes
4. PostgreSQL-backed test commands and results
5. unavailable checks, required approvals, and remaining risks

## Completion criteria

A schema change is complete when its forward migration matches the application mappings, existing data and rollout effects are understood, the relevant PostgreSQL migration path is tested where available, and live-environment actions and unverified assumptions are clearly identified.
