# domain/repository

Interfaces de repositório (ex: `AuthRepository`, `AccountRepository`, `PixRepository`).

As implementações (`*RepositoryImpl` e fakes) ficam em `data/repository` — esta pasta contém
apenas contratos, sem dependência de `data/`. Adicionadas por spec, ver `docs/specs/`.
