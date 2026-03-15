import { test, expect } from '@playwright/test'

test.describe('Crops', () => {
  test('crop catalog page loads', async ({ page }) => {
    await page.goto('/crops')
    await expect(page.locator('h1, h2, [data-testid="crops-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('crop detail page loads', async ({ page }) => {
    await page.goto('/crops')
    const card = page.locator('[data-testid="crop-card"], .crop-card').first()
    if (await card.isVisible({ timeout: 3000 }).catch(() => false)) {
      await card.click()
      await expect(page.url()).toContain('/crops/')
    }
  })
})
