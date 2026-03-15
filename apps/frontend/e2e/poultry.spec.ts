import { test, expect } from '@playwright/test'

test.describe('Poultry', () => {
  test('flocks page loads', async ({ page }) => {
    await page.goto('/poultry/flocks')
    await expect(page.locator('h1, h2, [data-testid="flocks-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('suppliers page loads', async ({ page }) => {
    await page.goto('/poultry/suppliers')
    await expect(page.locator('h1, h2, [data-testid="suppliers-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('customers page loads', async ({ page }) => {
    await page.goto('/poultry/customers')
    await expect(page.locator('h1, h2, [data-testid="customers-heading"]').first()).toBeVisible({ timeout: 5000 })
  })
})
