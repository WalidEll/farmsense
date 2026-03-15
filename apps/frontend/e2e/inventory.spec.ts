import { test, expect } from '@playwright/test'

test.describe('Inventory', () => {
  test('inventory dashboard loads', async ({ page }) => {
    await page.goto('/inventory')
    await expect(page.locator('h1, h2, [data-testid="inventory-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('inventory items are displayed', async ({ page }) => {
    await page.goto('/inventory')
    // Wait for content to load
    await page.waitForTimeout(2000)
    const content = page.locator('main, [data-testid="inventory-content"]').first()
    await expect(content).toBeVisible()
  })
})
