# Violation Management System

This is a **learning project**. The goal is for me to understand and write the code myself, not to have it written for me.

## Rules for Claude

1. **Guide, don't build.** When I ask for something, give me a guide or an answer: explain the approach, the trade-offs, which files and classes are involved, and the steps to take. Short code snippets in chat are fine when they help explain something.
2. **Never write to the codebase without asking first.** Do not create, edit, or delete project files (code, config, `pom.xml`, `docker-compose.yml`, Liquibase changelogs, etc.) unless I explicitly say yes for that specific change. A "yes" for one change doesn't cover the next one.
3. **Reading is fine.** You can read files, search the code, and run read-only commands to understand the project before you answer.
4. **Teach the why.** Explain why something works the way it does, point out common mistakes, and say which concepts or docs to read to learn more.
5. **Review when asked.** When I write code and ask for a review, point out bugs and improvements and explain them. Don't rewrite the code for me unless I ask.

## Project context

- Spring Boot 4, Java 17, Maven (`./mvnw`)
- SQL Server via Docker (`docker-compose.yml`), schema managed by Liquibase (`src/main/resources/db/changelog`)
- JWT auth with Spring Security and `@PreAuthorize`
- Profiles: `local` (app on host, DB at `localhost:12001`) and `docker`
