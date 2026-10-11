---
name: documentation-language-standard
description: "Use when producing documentation, comments, commit messages, or generated code artifacts; keep text in English unless the user explicitly asks otherwise."
applyTo: "**"
---

## Scope

- Applies to all project documentation, comments, docstrings, commit messages, and generated code artifacts.
- Use this standard across the repository even when the user writes in another language.
- This rule covers writing quality and language consistency only. It does not define approval flow, README policy, or API contract design.

## Required behavior

- Write all project documentation, inline comments, docstrings, commit messages, and repository text in English.
- Keep code artifacts and documentation in English even when the user writes in German or another language.
- Prefer precise, unambiguous wording over casual phrasing.
- Keep terminology consistent across files, comments, and documentation.

## Exception

- If the user explicitly requests a German chat response, the chat reply may be in German.
- Repository text, code comments, documentation, generated artifacts, and file content must still remain in English.

## Do not

- Do not mix English and German in repository documentation or code comments.
- Do not leave ambiguous, vague, or untranslated wording in project artifacts.
- Do not create documentation that contradicts the current implementation.

## Documentation quality standard

- Keep wording clear, direct, and maintainable.
- Match the repository’s existing English-only conventions in every file you create or modify.
