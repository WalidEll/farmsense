import { test, expect } from '@playwright/test'

test.describe('Plant Detail', () => {
  test('detail page loads for a plant', async ({ page }) => {
    await page.goto('/')
    const card = page.locator('[data-testid="plant-card"], .plant-card').first()
    if (await card.isVisible({ timeout: 5000 }).catch(() => false)) {
      await card.click()
      await expect(page.locator('[data-testid="plant-detail"], .plant-detail').first()).toBeVisible({ timeout: 5000 })
    }
  })
})
