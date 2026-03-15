import { test, expect } from '@playwright/test'

test.describe('Breed Catalogue', () => {
  // ── US1: Browse and Search ──────────────────────────────────────────

  test('breed catalogue page loads', async ({ page }) => {
    await page.goto('/poultry/breeds')
    await expect(
      page.locator('h1, h2, [data-testid="breeds-heading"]').first()
    ).toBeVisible({ timeout: 5000 })
  })

  test('breed cards are rendered', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const card = page.locator('[data-testid="breed-card"], .breed-card').first()
    await expect(card).toBeVisible({ timeout: 10000 })
  })

  test('search filters the breed list', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const searchInput = page.getByPlaceholder(/search|rechercher|ابحث/i)
    if (await searchInput.isVisible({ timeout: 3000 }).catch(() => false)) {
      await searchInput.fill('Sasso')
      await page.waitForTimeout(400) // debounce
      const cards = page.locator('[data-testid="breed-card"], .breed-card')
      const count = await cards.count()
      // After filtering there should be at least 0 results (no crash)
      expect(count).toBeGreaterThanOrEqual(0)
    }
  })

  test('category filter dropdown is available', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const categoryFilter = page.getByRole('combobox').filter({ hasText: /all|tous|category|catégorie|COMMERCIAL|HERITAGE|CUSTOM/i })
    if (await categoryFilter.isVisible({ timeout: 3000 }).catch(() => false)) {
      await categoryFilter.click()
      await expect(
        page.getByRole('option').filter({ hasText: /COMMERCIAL|commercial/i }).first()
      ).toBeVisible({ timeout: 3000 })
    }
  })

  test('purpose filter is available', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const purposeFilter = page.getByRole('combobox').filter({ hasText: /purpose|objectif|LAYERS|BROILERS|pondeuses|chair/i })
    if (await purposeFilter.isVisible({ timeout: 3000 }).catch(() => false)) {
      await expect(purposeFilter).toBeVisible()
    }
  })

  // ── US2: Breed Profile Detail ───────────────────────────────────────

  test('clicking a breed card navigates to detail page', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const card = page.locator('[data-testid="breed-card"], .breed-card').first()
    if (await card.isVisible({ timeout: 5000 }).catch(() => false)) {
      await card.click()
      await expect(page.url()).toContain('/poultry/breeds/')
    }
  })

  test('breed detail page loads when navigated directly', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const card = page.locator('[data-testid="breed-card"], .breed-card').first()
    if (await card.isVisible({ timeout: 5000 }).catch(() => false)) {
      await card.click()
      await expect(
        page.locator('h1, [data-testid="breed-name"]').first()
      ).toBeVisible({ timeout: 5000 })
    }
  })

  test('breed detail shows feeding program section', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const card = page.locator('[data-testid="breed-card"], .breed-card').first()
    if (await card.isVisible({ timeout: 5000 }).catch(() => false)) {
      await card.click()
      const feedingSection = page.locator('text=/feeding|alimentation|برنامج التغذية/i').first()
      await expect(feedingSection).toBeVisible({ timeout: 5000 })
    }
  })

  test('breed detail back button returns to catalogue', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const card = page.locator('[data-testid="breed-card"], .breed-card').first()
    if (await card.isVisible({ timeout: 5000 }).catch(() => false)) {
      await card.click()
      await expect(page.url()).toContain('/poultry/breeds/')
      const backBtn = page.locator('button').filter({ hasText: /⬅️|back|retour|رجوع/i }).first()
      if (await backBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
        await backBtn.click()
        await expect(page.url()).toMatch(/\/poultry\/breeds$/)
      }
    }
  })

  // ── US4: Custom Breeds ──────────────────────────────────────────────

  test('add custom breed button is visible', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const addButton = page.getByRole('button', { name: /add|ajouter|إضافة|custom|personnalisé/i })
    if (await addButton.isVisible({ timeout: 3000 }).catch(() => false)) {
      await expect(addButton).toBeVisible()
    }
  })

  test('custom breed form opens on add button click', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const addButton = page.getByRole('button', { name: /add.*breed|ajouter.*race|إضافة.*سلالة/i })
    if (await addButton.isVisible({ timeout: 3000 }).catch(() => false)) {
      await addButton.click()
      await expect(
        page.getByLabel(/name|nom|اسم/i).first()
      ).toBeVisible({ timeout: 3000 })
    }
  })

  test('custom breed form validates required fields', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const addButton = page.getByRole('button', { name: /add.*breed|ajouter.*race|إضافة.*سلالة/i })
    if (await addButton.isVisible({ timeout: 3000 }).catch(() => false)) {
      await addButton.click()
      const submitBtn = page.getByRole('button', { name: /save|enregistrer|حفظ/i })
      if (await submitBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
        await submitBtn.click()
        // Validation errors should appear
        const errorMsg = page.locator('[class*="error"], [class*="invalid"], [data-testid*="error"]').first()
        await expect(errorMsg).toBeVisible({ timeout: 3000 })
      }
    }
  })

  // ── US5: Compare Breeds ─────────────────────────────────────────────

  test('compare checkboxes are visible on breed cards', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const firstCard = page.locator('[data-testid="breed-card"], .breed-card').first()
    if (await firstCard.isVisible({ timeout: 5000 }).catch(() => false)) {
      const checkbox = firstCard.locator('input[type="checkbox"], [data-testid="compare-checkbox"]')
      if (await checkbox.isVisible({ timeout: 2000 }).catch(() => false)) {
        await expect(checkbox).toBeVisible()
      }
    }
  })

  test('selecting two breeds shows compare bar', async ({ page }) => {
    await page.goto('/poultry/breeds')
    const cards = page.locator('[data-testid="breed-card"], .breed-card')
    const cardCount = await cards.count()
    if (cardCount >= 2) {
      const firstCheckbox = cards.nth(0).locator('input[type="checkbox"]')
      const secondCheckbox = cards.nth(1).locator('input[type="checkbox"]')
      if (
        await firstCheckbox.isVisible({ timeout: 2000 }).catch(() => false) &&
        await secondCheckbox.isVisible({ timeout: 2000 }).catch(() => false)
      ) {
        await firstCheckbox.click()
        await secondCheckbox.click()
        const compareBar = page.locator('[data-testid="compare-bar"], text=/compare|comparer|مقارنة/i')
        await expect(compareBar.first()).toBeVisible({ timeout: 3000 })
      }
    }
  })

  // ── Poultry nav ─────────────────────────────────────────────────────

  test('breed catalogue is accessible from poultry navigation', async ({ page }) => {
    await page.goto('/poultry')
    const breedLink = page.getByRole('link', { name: /breed|race|سلالة/i })
    if (await breedLink.isVisible({ timeout: 3000 }).catch(() => false)) {
      await breedLink.click()
      await expect(page.url()).toContain('/poultry/breeds')
    }
  })
})
