# Sample JWT Access Tokens

Generated with secret: `change-me-very-long-secret-key-at-least-256-bits`

| User | Authorities | Token |
|------|-------------|-------|
| system_admin | role:write, user:read, user:write, user:delete, role:read | `eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6WyJ1c2VyOmRlbGV0ZSIsInVzZXI6cmVhZCIsInVzZXI6d3JpdGUiLCJyb2xlOnJlYWQiLCJyb2xlOndyaXRlIl0sInN1YiI6InN5c3RlbV9hZG1pbiIsImlhdCI6MTc4NTMyMDQ0MywiZXhwIjoxNzg1MzIxMzQzfQ.cTmyqdT547fO3YRyXXiP-Ey6YZLjTYSqUnnkR1ydHeZGtfuTFWW2ZJEorXTJQnr3` |
| institution_admin | user:write, user:read, role:read | `eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6WyJ1c2VyOnJlYWQiLCJyb2xlOnJlYWQiLCJ1c2VyOndyaXRlIl0sInN1YiI6Imluc3RpdHV0aW9uX2FkbWluIiwiaWF0IjoxNzg1MzIwNDQzLCJleHAiOjE3ODUzMjEzNDN9.di8uE9ayZDAgP3VfP58_aihxukkYcvqgg1oRdyg-soPeGKRgeyPiL_d9bgg854XF` |
| registrar | user:write, user:read | `eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6WyJ1c2VyOndyaXRlIiwidXNlcjpyZWFkIl0sInN1YiI6InJlZ2lzdHJhciIsImlhdCI6MTc4NTMyMDQ0MywiZXhwIjoxNzg1MzIxMzQzfQ.YnMpjoBAh9ysX1wUJ8HlLtmDdje1hfpKoVbICV-OGVfxsDPXxppkqfMHQfvhP9AW` |
| verifier | user:read | `eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6WyJ1c2VyOnJlYWQiXSwic3ViIjoidmVyaWZpZXIiLCJpYXQiOjE3ODUzMjA0NDMsImV4cCI6MTc4NTMyMTM0M30.UEnnsp_aRVexGhq_TnygkZy3xwm0Gzqn1TxwjC2ye4GSbP5VmaTIo1tyfuXucxHL` |
| auditor | role:read, user:read | `eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6WyJ1c2VyOnJlYWQiLCJyb2xlOnJlYWQiXSwic3ViIjoiYXVkaXRvciIsImlhdCI6MTc4NTMyMDQ0MywiZXhwIjoxNzg1MzIxMzQzfQ.FYZEcbtzzOuMfZ3aDoSqPxa5o8Xw_84anQPpUpLurywKbDtBirt58SjA-LZ_NkWq` |
