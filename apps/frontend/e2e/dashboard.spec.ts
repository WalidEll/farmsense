import { test, expect } from '@playwright/test'

test.describe('Dashboard', () => {
  test('displays plant cards', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('[data-testid="plant-card"], .plant-card').first()).toBeVisible({ timeout: 10000 })
  })

  test('navigation sidebar is visible', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('nav, [data-testid="sidebar"]').first()).toBeVisible()
  })
})
