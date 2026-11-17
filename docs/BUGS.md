# Bugs

- [ ] Operator assignment race: two operators can claim the same violation (`ViolationService.assignViolations`), needs locking or a conditional update
- [ ] GET /violations assigns violations as a side effect, move to `POST /violations/claim`
- [ ] `GetViolationResponse` has no `id`
- [ ] NPE in `GetViolationResponse.from` when a manual violation has no inspector
- [ ] GET /violations returns 201 instead of 200
- [ ] `getViolations` fallback returns `findAll()` for any unknown role, no paging
- [ ] `assignViolations` loads all unassigned rows, ignores status, queries twice
- [ ] `@Data` on entities: `toString` loads lazy relations; `ManualViolation` equals ignores the parent class
- [ ] `createViolation`: wrong "region doesn't exist" message for violation type, duplicate type lookup, `RuntimeException` → 500 instead of 400
