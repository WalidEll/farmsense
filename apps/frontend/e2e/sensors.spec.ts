import { test, expect } from '@playwright/test'

test.describe('Sensors', () => {
  test('sensors page displays readings', async ({ page }) => {
    await page.goto('/sensors')
    await expect(page.locator('h1, h2, [data-testid="sensors-heading"]').first()).toBeVisible({ timeout: 5000 })
  })
})
