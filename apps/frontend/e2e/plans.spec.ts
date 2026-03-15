import { test, expect } from '@playwright/test'

test.describe('Crop Plans', () => {
  test('plans list page loads', async ({ page }) => {
    await page.goto('/plans')
    await expect(page.locator('h1, h2, [data-testid="plans-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('plan detail page loads', async ({ page }) => {
    await page.goto('/plans')
    const card = page.locator('[data-testid="plan-card"], .plan-card').first()
    if (await card.isVisible({ timeout: 3000 }).catch(() => false)) {
      await card.click()
      await expect(page.url()).toContain('/plans/')
    }
  })
})
