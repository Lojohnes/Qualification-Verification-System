import { createTheme, type ThemeOptions } from '@mui/material/styles';

const baseTheme: ThemeOptions = {
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 600 },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: { borderRadius: 8 },
};

export const getLightTheme = () =>
  createTheme({
    ...baseTheme,
    palette: {
      mode: 'light',
      primary: {
        main: '#1565c0',
        contrastText: '#ffffff',
      },
      secondary: {
        main: '#455a64',
      },
      background: {
        default: '#f4f6f8',
        paper: '#ffffff',
      },
    },
  });

export const getDarkTheme = () =>
  createTheme({
    ...baseTheme,
    palette: {
      mode: 'dark',
      primary: {
        main: '#90caf9',
        contrastText: '#000000',
      },
      secondary: {
        main: '#b0bec5',
      },
      background: {
        default: '#121212',
        paper: '#1e1e1e',
      },
    },
  });
