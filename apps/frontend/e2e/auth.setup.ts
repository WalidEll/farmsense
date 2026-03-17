import { test as setup, expect } from '@playwright/test'

const authFile = 'e2e/.auth/user.json'

setup('authenticate', async ({ page, request }) => {
  const apiUrl = process.env.API_URL ?? 'http://localhost:8080'

  // 1. Idempotently register the test user via API
  // We use the full backend URL to ensure we hit the API directly
  const registerRes = await request.post(`${apiUrl}/api/v1/auth/register`, {
    data: {
      email: 'test@farmsense.ma',
      password: 'Test1234!',
      name: 'Test User',
      lang: 'FR'
    }
  })
  
  // Only ignore 409 Conflict (user already exists), fail on any other error
  if (registerRes.status() !== 409) {
    expect(registerRes.ok(), `Registration failed with status ${registerRes.status()}`).toBeTruthy()
  }

  // 2. Perform login via UI
  await page.goto('/login')
  
  // Wait for the form to be loaded
  await page.waitForSelector('#email')
  
  await page.locator('#email').fill('test@farmsense.ma')
  await page.locator('#password').fill('Test1234!')
  
  // Click login button. The button might have different text depending on locale
  await page.getByRole('button', { name: /login|connexion|se connecter|دخول/i }).click()

  // 3. Wait for redirect to dashboard or poultry view
  // Use a regex to allow for different landing pages (root or /poultry)
  await expect(page).toHaveURL(/\/poultry|\/$/, { timeout: 10000 })
  
  // Verify that we are actually logged in by checking for the logout button
  await expect(page.getByRole('button', { name: /logout|déconnecter|خروج/i })).toBeVisible()

  // 4. Save storage state for all subsequent tests
  await page.context().storageState({ path: authFile })
})
