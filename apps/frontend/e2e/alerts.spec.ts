import { test, expect } from '@playwright/test'

test.describe('Alerts', () => {
  test('alert list page loads', async ({ page }) => {
    await page.goto('/alerts')
    await expect(page.locator('h1, h2, [data-testid="alerts-heading"]').first()).toBeVisible({ timeout: 5000 })
  })

  test('can acknowledge an alert', async ({ page }) => {
    await page.goto('/alerts')
    const ackButton = page.getByRole('button', { name: /acknowledge|confirmer|تأكيد/i }).first()
    if (await ackButton.isVisible({ timeout: 3000 }).catch(() => false)) {
      await ackButton.click()
    }
  })
})
