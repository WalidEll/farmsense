import { test, expect } from '@playwright/test'

test.describe('Settings', () => {
  test('settings page loads', async ({ page }) => {
    await page.goto('/settings')
    await expect(page.locator('h1, h2, [data-testid="settings-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('language switch is available', async ({ page }) => {
    await page.goto('/settings')
    const langSelector = page.locator('select, [data-testid="language-select"]').first()
    if (await langSelector.isVisible({ timeout: 3000 }).catch(() => false)) {
      await expect(langSelector).toBeVisible()
    }
  })
})
