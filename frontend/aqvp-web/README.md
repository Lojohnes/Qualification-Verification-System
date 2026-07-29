# AQVP Web Frontend

The React-based web frontend for the Academic Qualification Verification Platform (AQVP).

## Technology Stack

- React 19
- TypeScript 5.7
- Vite 6
- Material UI (MUI) 6
- React Router v7
- Redux Toolkit
- Axios
- React Hook Form + Yup
- ESLint + Prettier
- Chart.js + React Icons

## Project Structure

```
frontend/aqvp-web/
├── public/                 # Static assets
├── src/
│   ├── components/         # Reusable UI components (atomic design)
│   │   ├── auth/           # Authentication route guards
│   │   ├── layout/         # Layout components (TopNav, Sidebar, Footer, Breadcrumbs)
│   │   └── ui/             # Reusable UI primitives (cards, tables, dialogs, spinners)
│   ├── config/             # Axios configuration and interceptors
│   ├── constants/          # Routes, API endpoints, messages, storage keys
│   ├── contexts/           # React contexts (theme, snackbar)
│   ├── features/           # Feature-based modules
│   │   ├── auth/           # Login, forgot password, reset password
│   │   ├── dashboard/      # Dashboard page and widgets
│   │   └── identity/       # Users, roles, and permissions pages
│   ├── hooks/              # Custom React hooks (useAuth, typed Redux hooks)
│   ├── layouts/            # Page layouts (MainLayout)
│   ├── pages/              # Top-level pages (404, access denied, placeholders)
│   ├── store/              # Redux store
│   ├── theme.ts            # Light/dark MUI theme
│   ├── types/              # Shared TypeScript types
│   └── utils/              # Helpers (storage, formatters)
├── .env.example            # Environment variables template
├── .eslintrc.cjs           # ESLint configuration
├── .prettierrc             # Prettier configuration
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## Getting Started

### Prerequisites

- Node.js 20+ (LTS recommended)
- npm 10+

### Install Dependencies

```bash
cd frontend/aqvp-web
npm install
```

### Environment Variables

Copy `.env.example` to `.env.local` and update the values:

```bash
cp .env.example .env.local
```

Example:

```text
VITE_API_BASE_URL=http://localhost:8081
VITE_APP_NAME=AQVP
```

### Development Scripts

```bash
npm run dev          # Start Vite dev server on http://localhost:3000
npm run build        # Type-check and build for production
npm run preview      # Preview production build
npm run lint         # Run ESLint
npm run lint:fix     # Fix ESLint issues
npm run format       # Format code with Prettier
npm run format:check # Check Prettier formatting
```

## Authentication

The frontend integrates with the AQVP Identity module:

- `POST /api/v1/auth/login` — authenticate and receive JWT access/refresh tokens.
- `POST /api/v1/auth/refresh` — refresh expired access tokens.
- `POST /api/v1/auth/logout` — invalidate the refresh token.

Axios interceptors automatically attach the access token and attempt silent token refresh on `401` responses. If refresh fails, the user is shown a session expired dialog and redirected to the login page.

## Navigation

- **Dashboard** — overview cards, recent activity, system status, quick actions.
- **Identity**
  - Users
  - Roles
  - Permissions
- **Institution** — placeholder for Sprint 3.
- **Qualification** — placeholder for Sprint 3.
- **Verification** — placeholder for Sprint 4.
- **Documents** — placeholder for Sprint 4.
- **Audit** — placeholder for Sprint 5.
- **Reports** — placeholder for Sprint 5.
- **Settings** — placeholder for Sprint 6.

## Protected Routes

Protected routes require a valid access token. Public routes such as `/login`, `/forgot-password`, and `/reset-password` redirect authenticated users to the dashboard.

## Theme

The application supports light and dark modes. The toggle is available in the top navigation bar. The selected mode is persisted to `localStorage`.

## Code Style

- ESLint with React, TypeScript, and Prettier rules.
- Prettier for consistent formatting.
- Conventional file naming: PascalCase for components, camelCase for utilities.

## License

This project is proprietary and confidential.
