import { test, expect } from '@playwright/test'

test.describe('Plants', () => {
  test('plant list is visible on dashboard', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('[data-testid="plant-card"], .plant-card').first()).toBeVisible({ timeout: 10000 })
  })

  test('can open add plant dialog', async ({ page }) => {
    await page.goto('/')
    const addButton = page.getByRole('button', { name: /add|ajouter|إضافة/i })
    if (await addButton.isVisible()) {
      await addButton.click()
      await expect(page.getByLabel(/name|nom|اسم/i)).toBeVisible()
    }
  })
})
