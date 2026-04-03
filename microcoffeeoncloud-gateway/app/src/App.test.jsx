import { render, screen } from '@testing-library/react';
import { CookiesProvider } from 'react-cookie';
import App from './App';
import { test } from 'vitest';

test('renders App', () => {
  render(
    <CookiesProvider>
      <App />
    </CookiesProvider>
  );
  const linkElement = screen.getByText(/Microcoffee/i);
  expect(linkElement).toBeInTheDocument();
});
