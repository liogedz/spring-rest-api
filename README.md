# SPRING REST API

## Developing SpringBoot RestAPI

- SpringBoot Maven Rest-API project
- Storage - H2 local
- for learning purpose I do not use `lombok`
- Cleanest solution, respecting separation of concern
- rename `.env.example` to `.env`
- insert your `gmail` address as `EMAIL_USR` and security code from `Google security` as `EMAIL_PWD` in `.env`
- login with e-mail or name
- application uses 2FA
- test from IDE [request.http](requests.http) put your real email address to test and verification code received in that
  e-mail
- see Data Base tables in IDE, set absolute path is IntelliJ DB settings
- generate JwtSecret

```bash
openssl rand -hex 32
```

- continuously updating ...