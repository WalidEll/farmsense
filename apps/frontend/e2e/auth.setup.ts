import { test as setup, expect } from '@playwright/test'

const authFile = 'e2e/.auth/user.json'

setup('authenticate', async ({ page }) => {
  await page.goto('/login')
  await page.getByLabel(/email/i).fill('test@farmsense.ma')
  await page.getByLabel(/password/i).fill('Test1234!')
  await page.getByRole('button', { name: /login|connexion|دخول/i }).click()

  // Wait for redirect to dashboard
  await expect(page).toHaveURL('/')
  await page.context().storageState({ path: authFile })
})
