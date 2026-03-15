import { test, expect } from '@playwright/test'

test.use({ storageState: { cookies: [], origins: [] } })

test.describe('Authentication', () => {
  test('login form renders', async ({ page }) => {
    await page.goto('/login')
    await expect(page.getByLabel(/email/i)).toBeVisible()
    await expect(page.getByLabel(/password/i)).toBeVisible()
    await expect(page.getByRole('button', { name: /login|connexion|دخول/i })).toBeVisible()
  })

  test('login redirects to dashboard on success', async ({ page }) => {
    await page.goto('/login')
    await page.getByLabel(/email/i).fill('test@farmsense.ma')
    await page.getByLabel(/password/i).fill('Test1234!')
    await page.getByRole('button', { name: /login|connexion|دخول/i }).click()
    await expect(page).toHaveURL('/')
  })

  test('shows error on invalid credentials', async ({ page }) => {
    await page.goto('/login')
    await page.getByLabel(/email/i).fill('bad@farmsense.ma')
    await page.getByLabel(/password/i).fill('wrongpassword')
    await page.getByRole('button', { name: /login|connexion|دخول/i }).click()
    await expect(page.getByText(/error|erreur|خطأ/i)).toBeVisible()
  })

  test('register form renders', async ({ page }) => {
    await page.goto('/register')
    await expect(page.getByLabel(/email/i)).toBeVisible()
    await expect(page.getByLabel(/password/i)).toBeVisible()
    await expect(page.getByLabel(/name|nom|اسم/i)).toBeVisible()
  })
})
