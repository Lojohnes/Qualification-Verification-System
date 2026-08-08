import React from 'react';
import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { CssBaseline } from '@mui/material';

import { store } from '@/store/store';
import { setupAxiosInterceptors } from '@/config/axios';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { SnackbarProvider } from '@/contexts/SnackbarContext';
import App from '@/App';
import '@/index.css';

setupAxiosInterceptors(store);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Provider store={store}>
      <ThemeProvider>
        <BrowserRouter>
          <SnackbarProvider>
            <CssBaseline />
            <App />
          </SnackbarProvider>
        </BrowserRouter>
      </ThemeProvider>
    </Provider>
  </React.StrictMode>
);
