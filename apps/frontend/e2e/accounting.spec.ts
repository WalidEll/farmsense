import { test, expect } from '@playwright/test'

test.describe('Accounting', () => {
  test('accounting dashboard loads', async ({ page }) => {
    await page.goto('/accounting')
    await expect(page.locator('h1, h2, [data-testid="accounting-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('transactions page loads', async ({ page }) => {
    await page.goto('/accounting/transactions')
    await expect(page.locator('h1, h2, [data-testid="transactions-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('receipts page loads', async ({ page }) => {
    await page.goto('/accounting/receipts')
    await expect(page.locator('h1, h2, [data-testid="receipts-heading"]').first()).toBeVisible({ timeout: 5000 })
  })
})
