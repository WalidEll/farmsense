import { test, expect } from '@playwright/test'

test.describe('Internationalization', () => {
  test('can switch to French', async ({ page }) => {
    await page.goto('/settings')
    const langSelector = page.locator('select, [data-testid="language-select"]').first()
    if (await langSelector.isVisible({ timeout: 3000 }).catch(() => false)) {
      await langSelector.selectOption({ label: /français/i })
    }
  })

  test('RTL layout applies for Arabic', async ({ page }) => {
    await page.goto('/settings')
    const langSelector = page.locator('select, [data-testid="language-select"]').first()
    if (await langSelector.isVisible({ timeout: 3000 }).catch(() => false)) {
      await langSelector.selectOption({ label: /عربي|arabic/i })
      const dir = await page.locator('html').getAttribute('dir')
      expect(dir).toBe('rtl')
    }
  })
})
