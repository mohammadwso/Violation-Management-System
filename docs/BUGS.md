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

## Current assignment refactor (changelogs 030–034)

Compiles, but claim and accept are broken at runtime.

### P0: claim / accept don't work

- [x] Unassigned lookup uses `findByCurrentAssignmentOperatorId(null)`: the inner join through `currentAssignment` drops violations with no assignment, so it always returns "No Unassigned Cases". Use `findFirstByCurrentAssignmentIsNullAndStatusIdOrderByViolationDateAsc(NEW)`
- [x] `acceptViolation` checks status `NEW`, but claiming sets `ASSIGNED`, so every accept is rejected
- [x] `acceptViolation` calls `updateAssignmentHistory(violation.getId(), ...)`: passes the violation id as a history id. Use `history.getId()`
- [x] `@Data` on `Violation` ↔ `ViolationAssignmentHistory`: `toString`/`equals`/`hashCode` recurse forever (StackOverflowError on `log.info(..., violation)`). Add `@ToString.Exclude` + `@EqualsAndHashCode.Exclude` on both relation fields

### P1: wrong data

- [x] `GetViolationResponse.assignedOperatorId` returns the history row id (`getCurrentAssignment().getId()`), should be `getCurrentAssignment().getOperator().getId()`
- [x] Releasing old violations in `assignViolations` closes history and nulls the pointer but leaves status `ASSIGNED`; set it back to `NEW` or they are never picked up again
- [ ] `acceptViolation` doesn't null `current_assignment_id` after `DECIDED`, doesn't set `decidedAt`, and isn't `@Transactional`
- [ ] `Status.from` has no `EXPIRED` case → id `null` → NOT NULL violation on `status_id`
- [ ] Ending an assignment (history row + pointer + status) is done differently in each caller; move it into one helper, e.g. `endCurrentAssignment(violation, outcome, newStatus, now)` used by release / accept / reject

### P2: cleanup

- [ ] Redundant `save()` calls on managed entities inside `@Transactional` (dirty checking handles them); only new entities need `save()`
- [ ] Dead `if (violation.getCurrentAssignment() != null)` block in claim (an unassigned violation has no current assignment)
- [ ] Concurrent claim hits `ux_vah_one_open_per_violation` → `DataIntegrityViolationException` → 500; catch it and return a clear message
- [ ] `addNewAssignmentHistory` has an unused `statusCode` param; `getById` is deprecated (use `getReferenceById`)
- [ ] `throw new RuntimeException("some error")` when there is no current assignment; use a proper exception
- [ ] Leftover commented-out code, unused `Status` import in `ViolationRepo`, duplicate "Currently Assigned Cases" log line
