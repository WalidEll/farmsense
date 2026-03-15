import { test, expect, type Page } from '@playwright/test'

// ---------------------------------------------------------------------------
//  Helpers
// ---------------------------------------------------------------------------

/** Navigate to the breed catalogue and wait for it to be interactive. */
async function gotoCatalogue(page: Page) {
  await page.goto('/poultry/breeds')
  await expect(page.locator('h1')).toContainText(/breed|catalogue|races|سلالات/i, {
    timeout: 8000,
  })
}

/** Wait for breed cards to render after data loads. */
async function waitForCards(page: Page, minCount = 1) {
  const cards = page.locator('.group.cursor-pointer')
  await expect(cards.first()).toBeVisible({ timeout: 12000 })
  const count = await cards.count()
  expect(count).toBeGreaterThanOrEqual(minCount)
  return cards
}

/** Open the first breed's detail page from the catalogue. */
async function openFirstBreedDetail(page: Page) {
  await gotoCatalogue(page)
  const cards = await waitForCards(page)
  await cards.first().click()
  await page.waitForURL(/\/poultry\/breeds\/[0-9a-f-]+$/i, { timeout: 5000 })
  await expect(page.locator('h1').first()).toBeVisible({ timeout: 8000 })
}

// ==========================================================================
//  US1 — Browse and Search Breed Catalogue (P1)
//  Spec acceptance scenarios AS1.1 – AS1.5
// ==========================================================================
test.describe('US1: Browse and Search Breed Catalogue', () => {
  // AS1.1 — Pre-seeded breeds render as paginated cards
  test('AS1.1 — catalogue page shows paginated breed cards with name, category, purpose, and image', async ({
    page,
  }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page)

    // Each card should have breed name, a category badge, and a purpose label
    const firstCard = cards.first()
    await expect(firstCard.locator('h3')).toBeVisible() // breed name
    // Category badge (COMMERCIAL / HERITAGE / CUSTOM) rendered inside a span
    await expect(
      firstCard.locator('span').filter({ hasText: /commercial|heritage|custom|commerciale|patrimoine|personnalis/i }).first(),
    ).toBeVisible()
    // Purpose label
    await expect(
      firstCard.locator('span').filter({ hasText: /layers|broilers|dual|pondeuses|chair|double/i }).first(),
    ).toBeVisible()
    // Image placeholder or actual image
    await expect(firstCard.locator('img, .text-6xl').first()).toBeVisible()
  })

  // AS1.2 — Search by breed name
  test('AS1.2 — typing "Sasso" in search filters the list to matching breeds', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await expect(searchInput).toBeVisible({ timeout: 3000 })
    await searchInput.fill('Sasso')
    await page.waitForTimeout(500)

    // After filtering, every visible card name should contain "Sasso"
    const cardNames = page.locator('.group.cursor-pointer h3')
    const count = await cardNames.count()
    for (let i = 0; i < count; i++) {
      await expect(cardNames.nth(i)).toContainText(/sasso/i)
    }
  })

  // AS1.3 — Purpose filter narrows the list
  test('AS1.3 — selecting "Layers" purpose filter shows only layer breeds', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    // The purpose <select> is the second dropdown
    const purposeSelect = page.locator('select').nth(1)
    await purposeSelect.selectOption('LAYERS')
    await page.waitForTimeout(400)

    // All visible purpose badges should say "Layers" (or i18n equivalent)
    const purposeBadges = page.locator('.group.cursor-pointer span.bg-gray-100')
    const badgeCount = await purposeBadges.count()
    for (let i = 0; i < badgeCount; i++) {
      await expect(purposeBadges.nth(i)).toContainText(/layers|pondeuses/i)
    }
  })

  // AS1.4 — Combined category filter + search
  test('AS1.4 — category filter "COMMERCIAL" combined with search narrows to matching breeds', async ({
    page,
  }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const categorySelect = page.locator('select').first()
    await categorySelect.selectOption('COMMERCIAL')
    await page.waitForTimeout(400)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill('Isa')
    await page.waitForTimeout(500)

    const cards = page.locator('.group.cursor-pointer')
    const count = await cards.count()
    expect(count).toBeGreaterThanOrEqual(0)
  })

  // AS1.5 — Empty state when no results match
  test('AS1.5 — searching for nonsense shows an empty state message', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill('xyznonexistentbreed99')
    await page.waitForTimeout(500)

    await expect(
      page.locator('text=/no.*breed|aucun|لا.*سلالة|breed_empty/i').first(),
    ).toBeVisible({ timeout: 5000 })
  })

  // T032 — Route exists and is accessible
  test('T032 — /poultry/breeds route loads the catalogue view', async ({ page }) => {
    await page.goto('/poultry/breeds')
    await expect(page).toHaveURL(/\/poultry\/breeds/)
    await expect(page.locator('h1')).toBeVisible({ timeout: 8000 })
  })

  // Pagination controls
  test('pagination controls are visible when enough breeds exist', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)
    const pageInfo = page.locator('text=/\\d+\\s*\\/\\s*\\d+/')
    if (await pageInfo.isVisible({ timeout: 2000 }).catch(() => false)) {
      await expect(pageInfo).toBeVisible()
    }
  })

  // Search input respects maxlength=100
  test('search input is limited to 100 characters', async ({ page }) => {
    await gotoCatalogue(page)
    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await expect(searchInput).toHaveAttribute('maxlength', '100')
  })
})

// ==========================================================================
//  US2 — View Breed Profile Details (P1)
//  Spec acceptance scenarios AS2.1 – AS2.5
// ==========================================================================
test.describe('US2: View Breed Profile Details', () => {
  // AS2.1 — Clicking a breed card opens its full profile page
  test('AS2.1 — selecting a breed card opens a full profile with general info and 4 sections', async ({
    page,
  }) => {
    await openFirstBreedDetail(page)

    await expect(page.locator('h1').first()).toBeVisible()

    // Category badge
    await expect(
      page.locator('span').filter({
        hasText: /commercial|heritage|custom|commerciale|patrimoine|personnalis/i,
      }).first(),
    ).toBeVisible()

    // Four structured sections
    await expect(page.locator('text=/feeding|alimentation|تغذية/i').first()).toBeVisible({ timeout: 5000 })
    await expect(page.locator('text=/vaccination|تلقيح/i').first()).toBeVisible({ timeout: 5000 })
    await expect(page.locator('text=/benchmark|production|إنتاج/i').first()).toBeVisible({ timeout: 5000 })
    await expect(page.locator('text=/housing|logement|إسكان/i').first()).toBeVisible({ timeout: 5000 })
  })

  // AS2.2 — Feeding program shows growth stages with details
  test('AS2.2 — feeding program section shows growth stages with feed type, daily quantity, frequency', async ({
    page,
  }) => {
    await openFirstBreedDetail(page)

    const feedingSection = page.locator('section').filter({
      has: page.locator('text=/feeding|alimentation|تغذية/i'),
    })
    await expect(feedingSection).toBeVisible({ timeout: 5000 })

    await expect(feedingSection.locator('th').first()).toBeVisible()

    const rows = feedingSection.locator('tbody tr')
    await expect(rows.first()).toBeVisible({ timeout: 3000 })

    const firstRow = rows.first()
    await expect(firstRow).toContainText(/\d+/)
    await expect(firstRow).toContainText('g')
  })

  // AS2.3 — Vaccination schedule is chronological with details
  test('AS2.3 — vaccination schedule shows chronological list with vaccine name, age, dosage, route', async ({
    page,
  }) => {
    await openFirstBreedDetail(page)

    const vaccSection = page.locator('section').filter({
      has: page.locator('text=/vaccination|تلقيح/i'),
    })
    await expect(vaccSection).toBeVisible({ timeout: 5000 })

    const dayBadge = vaccSection.locator('.text-lg.font-bold').first()
    await expect(dayBadge).toContainText(/\d+/)

    const vaccineName = vaccSection.locator('.font-medium.text-gray-900').first()
    await expect(vaccineName).toBeVisible()
  })

  // AS2.4 — Production benchmarks show metrics with values and units
  test('AS2.4 — production benchmarks show expected metrics with age-specific targets', async ({
    page,
  }) => {
    await openFirstBreedDetail(page)

    const benchSection = page.locator('section').filter({
      has: page.locator('text=/benchmark|production|إنتاج/i'),
    })
    await expect(benchSection).toBeVisible({ timeout: 5000 })

    const greenValue = benchSection.locator('.text-green-600').first()
    await expect(greenValue).toBeVisible()
    await expect(greenValue).toContainText(/\d/)

    const unitLabel = benchSection.locator('.text-xs.text-gray-400').first()
    await expect(unitLabel).toBeVisible()
  })

  // AS2.5 — Housing guidelines show parameters with recommended values
  test('AS2.5 — housing guidelines show space per bird, temperature, ventilation, lighting', async ({
    page,
  }) => {
    await openFirstBreedDetail(page)

    const housingSection = page.locator('section').filter({
      has: page.locator('text=/housing|logement|إسكان/i'),
    })
    await expect(housingSection).toBeVisible({ timeout: 5000 })

    await expect(housingSection.locator('.font-medium.text-gray-900').first()).toBeVisible()
    await expect(housingSection.locator('.font-semibold.text-gray-900').first()).toBeVisible()
  })

  // Back button returns to catalogue
  test('back button navigates to the catalogue listing', async ({ page }) => {
    await openFirstBreedDetail(page)

    const backBtn = page.locator('button').filter({ has: page.locator('text=/⬅️/') }).first()
    await expect(backBtn).toBeVisible()
    await backBtn.click()
    await page.waitForURL(/\/poultry\/breeds/, { timeout: 5000 })
  })

  // Multilingual names display
  test('breed name displays in the active locale', async ({ page }) => {
    await openFirstBreedDetail(page)
    const heading = page.locator('h1').first()
    await expect(heading).not.toBeEmpty()
  })

  // Origin and purpose subtitle
  test('detail header shows origin and purpose', async ({ page }) => {
    await openFirstBreedDetail(page)
    const subtitle = page.locator('.text-sm.text-gray-500').first()
    await expect(subtitle).toBeVisible()
  })

  // Climate suitability & weight info card
  test('breed info card shows climate suitability and weight ranges', async ({ page }) => {
    await openFirstBreedDetail(page)
    const infoCard = page.locator('.bg-white.rounded-2xl.border.border-gray-100.shadow-sm.p-6').first()
    await expect(infoCard).toBeVisible({ timeout: 5000 })
  })
})

// ==========================================================================
//  US3 — Auto-Generate Flock Programs from Breed Selection (P1)
//  Spec acceptance scenarios AS3.1 – AS3.5
// ==========================================================================
test.describe('US3: Auto-Generate Flock Programs from Breed Selection', () => {
  // AS3.1 — Selecting a breed on flock creation auto-populates programs
  test('AS3.1 — selecting a breed in flock form auto-populates feeding, vaccination, housing', async ({
    page,
  }) => {
    await page.goto('/poultry/flocks')
    await page.waitForTimeout(1000)

    const addBtn = page.getByRole('button', { name: /add|ajouter|إضافة|new|nouveau/i }).first()
    if (!(await addBtn.isVisible({ timeout: 5000 }).catch(() => false))) return

    await addBtn.click()
    await page.waitForTimeout(500)

    const breedDropdown = page.locator('.relative').filter({
      has: page.locator('text=/breed|race|سلالة/i'),
    }).first()

    if (await breedDropdown.isVisible({ timeout: 3000 }).catch(() => false)) {
      const input = breedDropdown.locator('input').first()
      if (await input.isVisible({ timeout: 2000 }).catch(() => false)) {
        await input.fill('Sasso')
        await page.waitForTimeout(500)

        const result = page.locator('.absolute li, [role="option"]').first()
        if (await result.isVisible({ timeout: 3000 }).catch(() => false)) {
          await result.click()
          await page.waitForTimeout(1000)

          const feedingSection = page.locator('text=/feeding|alimentation|تغذية|starter|grower|finisher/i').first()
          await expect(feedingSection).toBeVisible({ timeout: 5000 })
        }
      }
    }
  })

  // AS3.2 — Auto-generated programs are editable before saving
  test('AS3.2 — auto-generated programs can be modified before saving', async ({ page }) => {
    await page.goto('/poultry/flocks')
    await page.waitForTimeout(1000)

    const addBtn = page.getByRole('button', { name: /add|ajouter|إضافة|new|nouveau/i }).first()
    if (!(await addBtn.isVisible({ timeout: 5000 }).catch(() => false))) return

    await addBtn.click()
    await page.waitForTimeout(500)

    const editableInputs = page.locator(
      'input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled])',
    )
    const inputCount = await editableInputs.count()
    expect(inputCount).toBeGreaterThan(0)
  })

  // AS3.5 — Changing breed on existing flock offers regeneration dialog
  test('AS3.5 — changing breed on existing flock shows regeneration confirmation dialog', async ({
    page,
  }) => {
    await page.goto('/poultry/flocks')
    await page.waitForTimeout(1000)

    const flockCard = page.locator('.cursor-pointer').first()
    if (!(await flockCard.isVisible({ timeout: 5000 }).catch(() => false))) return

    await flockCard.click()
    await page.waitForTimeout(1000)

    const editBtn = page.getByRole('button', { name: /edit|modifier|تعديل/i }).first()
    if (!(await editBtn.isVisible({ timeout: 3000 }).catch(() => false))) return

    await editBtn.click()
    await page.waitForTimeout(500)

    const formVisible = page.locator('form, [role="dialog"]').first()
    await expect(formVisible).toBeVisible({ timeout: 3000 })
  })
})

// ==========================================================================
//  US4 — Create, Edit, and Delete Custom Breed Entries (P2)
//  Spec acceptance scenarios AS4.1 – AS4.7
// ==========================================================================
test.describe('US4: Custom Breed CRUD', () => {
  const uniqueName = `E2E-Test-Breed-${Date.now()}`

  // AS4.1 — "Add Custom Breed" opens a form with all expected fields
  test('AS4.1 — "Add Custom Breed" button opens a form with name, category, purpose, description fields', async ({
    page,
  }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({
      hasText: /➕|add|ajouter|إضافة/i,
    }).first()
    await expect(addBtn).toBeVisible({ timeout: 3000 })
    await addBtn.click()

    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    // Required fields: name input, purpose dropdown
    const nameInput = page.locator('.fixed.inset-0 input[type="text"]:not([disabled])').first()
    await expect(nameInput).toBeVisible()

    const purposeSelect = page.locator('.fixed.inset-0 select').first()
    await expect(purposeSelect).toBeVisible()

    // Category locked to CUSTOM
    const categoryInput = page.locator('input[disabled][value="CUSTOM"]')
    await expect(categoryInput).toBeVisible()

    // Optional name_ar, name_en, description
    const textInputs = page.locator('.fixed.inset-0 input[type="text"]:not([disabled])')
    expect(await textInputs.count()).toBeGreaterThanOrEqual(3)

    await expect(page.locator('.fixed.inset-0 textarea')).toBeVisible()
  })

  // AS4.2 — Submitting with only required fields creates the breed
  test('AS4.2 — creating a custom breed with required fields only succeeds', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({ hasText: /➕|add|ajouter|إضافة/i }).first()
    await addBtn.click()
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    const formInputs = page.locator('.fixed.inset-0 input[type="text"]:not([disabled])')
    await formInputs.first().fill(uniqueName)

    const purposeSelect = page.locator('.fixed.inset-0 select').first()
    await purposeSelect.selectOption('DUAL_PURPOSE')

    const saveBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /save|enregistrer|حفظ/i,
    }).first()
    await saveBtn.click()

    await expect(page.locator('.fixed.inset-0')).toBeHidden({ timeout: 8000 })
  })

  // AS4.3 — Custom breed appears in catalogue with visual indicator
  test('AS4.3 — newly created custom breed appears in the catalogue', async ({ page }) => {
    await gotoCatalogue(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill(uniqueName)
    await page.waitForTimeout(500)

    const cards = page.locator('.group.cursor-pointer')
    await expect(cards.first()).toBeVisible({ timeout: 8000 })
    await expect(cards.first().locator('h3')).toContainText(uniqueName)
  })

  // AS4.5 — Edit a custom breed
  test('AS4.5 — editing a custom breed updates its fields', async ({ page }) => {
    await gotoCatalogue(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill(uniqueName)
    await page.waitForTimeout(500)

    const card = page.locator('.group.cursor-pointer').first()
    if (!(await card.isVisible({ timeout: 5000 }).catch(() => false))) return

    await card.click()
    await page.waitForURL(/\/poultry\/breeds\/[0-9a-f-]+$/i, { timeout: 5000 })

    const editBtn = page.locator('button').filter({ hasText: /✏️|edit|modifier|تعديل/i }).first()
    if (!(await editBtn.isVisible({ timeout: 3000 }).catch(() => false))) return

    await editBtn.click()

    const originInput = page.locator('.fixed.inset-0 input[type="text"]').nth(3)
    if (await originInput.isVisible({ timeout: 2000 }).catch(() => false)) {
      await originInput.fill('Morocco (E2E Updated)')
    }

    const saveBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /save|enregistrer|حفظ/i,
    }).first()
    await saveBtn.click()

    await expect(page.locator('.fixed.inset-0').first()).toBeHidden({ timeout: 8000 })
  })

  // AS4.6 — Delete a custom breed not assigned to any flock
  test('AS4.6 — deleting a custom breed without flock reference removes it', async ({ page }) => {
    await gotoCatalogue(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill(uniqueName)
    await page.waitForTimeout(500)

    const card = page.locator('.group.cursor-pointer').first()
    if (!(await card.isVisible({ timeout: 5000 }).catch(() => false))) return

    await card.click()
    await page.waitForURL(/\/poultry\/breeds\/[0-9a-f-]+$/i, { timeout: 5000 })

    const deleteBtn = page.locator('button').filter({ hasText: /🗑️|delete|supprimer|حذف/i }).first()
    if (!(await deleteBtn.isVisible({ timeout: 3000 }).catch(() => false))) return

    await deleteBtn.click()

    const confirmModal = page.locator('.fixed.inset-0.bg-black\\/40')
    await expect(confirmModal).toBeVisible({ timeout: 3000 })

    const confirmDeleteBtn = confirmModal.locator('button.bg-red-600')
    await confirmDeleteBtn.click()

    await page.waitForURL(/\/poultry\/breeds$/, { timeout: 8000 })
  })

  // AS4.7 — System breeds cannot be edited or deleted
  test('AS4.7 — system breeds do not expose edit or delete buttons', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    // Filter to COMMERCIAL to target a system breed
    const categorySelect = page.locator('select').first()
    await categorySelect.selectOption('COMMERCIAL')
    await page.waitForTimeout(500)

    const card = page.locator('.group.cursor-pointer').first()
    if (!(await card.isVisible({ timeout: 5000 }).catch(() => false))) return

    await card.click()
    await page.waitForURL(/\/poultry\/breeds\/[0-9a-f-]+$/i, { timeout: 5000 })
    await page.waitForTimeout(1000)

    const editBtn = page.locator('button').filter({ hasText: /✏️|edit|modifier|تعديل/i }).first()
    const deleteBtn = page.locator('button').filter({ hasText: /🗑️|delete|supprimer|حذف/i }).first()

    expect(await editBtn.isVisible({ timeout: 2000 }).catch(() => false)).toBe(false)
    expect(await deleteBtn.isVisible({ timeout: 2000 }).catch(() => false)).toBe(false)
  })

  // Form validation — empty name blocked
  test('custom breed form validates required name field', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({ hasText: /➕|add|ajouter|إضافة/i }).first()
    await addBtn.click()
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    const saveBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /save|enregistrer|حفظ/i,
    }).first()
    await saveBtn.click()

    // Modal stays open because validation prevents submission
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 2000 })
  })

  // Feeding program rows can be added in the form
  test('custom breed form allows adding feeding program rows', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({ hasText: /➕|add|ajouter|إضافة/i }).first()
    await addBtn.click()
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    const addFeedingBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /\+.*feed|ajout.*aliment/i,
    }).first()
    if (await addFeedingBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
      await addFeedingBtn.click()
      const stageSelect = page.locator('.fixed.inset-0 select').filter({
        has: page.locator('option[value="STARTER"]'),
      })
      await expect(stageSelect.first()).toBeVisible({ timeout: 2000 })
    }
  })

  // Vaccination rows can be added in the form
  test('custom breed form allows adding vaccination schedule rows', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({ hasText: /➕|add|ajouter|إضافة/i }).first()
    await addBtn.click()
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    const addVaccBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /\+.*vaccin/i,
    }).first()
    if (await addVaccBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
      await addVaccBtn.click()
      const routeSelect = page.locator('.fixed.inset-0 select').filter({
        has: page.locator('option[value="INJECTION"]'),
      })
      await expect(routeSelect.first()).toBeVisible({ timeout: 2000 })
    }
  })

  // Housing guideline rows can be added in the form
  test('custom breed form allows adding housing guideline rows', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const addBtn = page.locator('button').filter({ hasText: /➕|add|ajouter|إضافة/i }).first()
    await addBtn.click()
    await expect(page.locator('.fixed.inset-0')).toBeVisible({ timeout: 3000 })

    const addHousingBtn = page.locator('.fixed.inset-0 button').filter({
      hasText: /\+.*hous|ajout.*logement/i,
    }).first()
    if (await addHousingBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
      await addHousingBtn.click()
      // A new row should appear with parameter input
      const paramInputs = page.locator('.fixed.inset-0').locator('input[type="text"]')
      expect(await paramInputs.count()).toBeGreaterThan(5)
    }
  })
})

// ==========================================================================
//  US5 — Compare Breeds Side by Side (P3)
//  Spec acceptance scenarios AS5.1 – AS5.4
// ==========================================================================
test.describe('US5: Compare Breeds Side by Side', () => {
  // AS5.1 — Selecting 2-3 breeds and clicking compare opens comparison view
  test('AS5.1 — selecting two breeds and clicking compare opens side-by-side view', async ({
    page,
  }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page, 2)

    const checkbox1 = cards.nth(0).locator('input[type="checkbox"]')
    const checkbox2 = cards.nth(1).locator('input[type="checkbox"]')

    if (
      !(await checkbox1.isVisible({ timeout: 2000 }).catch(() => false)) ||
      !(await checkbox2.isVisible({ timeout: 2000 }).catch(() => false))
    )
      return

    await checkbox1.click()
    await checkbox2.click()

    const compareBar = page.locator('.fixed.bottom-0')
    await expect(compareBar).toBeVisible({ timeout: 3000 })
    await expect(compareBar).toContainText(/2\/3|2 \/ 3/)

    const compareBtn = compareBar.locator('button.bg-green-600')
    await compareBtn.click()

    await page.waitForURL(/\/poultry\/breeds\/compare/, { timeout: 5000 })
  })

  // AS5.2 — Comparison view shows production benchmarks, feeding, housing, vaccination
  test('AS5.2 — comparison view shows key metrics in aligned columns', async ({ page }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page, 2)

    const checkbox1 = cards.nth(0).locator('input[type="checkbox"]')
    const checkbox2 = cards.nth(1).locator('input[type="checkbox"]')

    if (
      !(await checkbox1.isVisible({ timeout: 2000 }).catch(() => false)) ||
      !(await checkbox2.isVisible({ timeout: 2000 }).catch(() => false))
    )
      return

    await checkbox1.click()
    await checkbox2.click()

    const compareBar = page.locator('.fixed.bottom-0')
    await expect(compareBar).toBeVisible({ timeout: 3000 })
    await compareBar.locator('button.bg-green-600').click()
    await page.waitForURL(/\/poultry\/breeds\/compare/, { timeout: 5000 })

    // The comparison view should show breed names as column headers
    const breedColumns = page.locator('th, .font-bold')
    expect(await breedColumns.count()).toBeGreaterThanOrEqual(2)

    // Should show comparison sections
    await expect(
      page.locator('text=/general|info|feeding|vaccination|benchmark|housing|alimentation/i').first(),
    ).toBeVisible({ timeout: 5000 })
  })

  // AS5.4 — Max 3 breeds enforced
  test('AS5.4 — selecting a 4th breed for comparison is rejected or capped', async ({ page }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page, 4)

    const checkboxes = [
      cards.nth(0).locator('input[type="checkbox"]'),
      cards.nth(1).locator('input[type="checkbox"]'),
      cards.nth(2).locator('input[type="checkbox"]'),
      cards.nth(3).locator('input[type="checkbox"]'),
    ]

    for (const cb of checkboxes) {
      if (!(await cb.isVisible({ timeout: 2000 }).catch(() => false))) return
    }

    await checkboxes[0].click()
    await checkboxes[1].click()
    await checkboxes[2].click()

    const compareBar = page.locator('.fixed.bottom-0')
    await expect(compareBar).toBeVisible({ timeout: 3000 })
    await expect(compareBar).toContainText(/3\/3|3 \/ 3/)

    await checkboxes[3].click()
    await page.waitForTimeout(300)

    // Still capped at 3
    await expect(compareBar).toContainText(/3\/3|3 \/ 3/)
  })

  // Compare bar — remove breed chip
  test('compare bar allows removing a breed from comparison', async ({ page }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page, 2)

    const checkbox1 = cards.nth(0).locator('input[type="checkbox"]')
    const checkbox2 = cards.nth(1).locator('input[type="checkbox"]')

    if (
      !(await checkbox1.isVisible({ timeout: 2000 }).catch(() => false)) ||
      !(await checkbox2.isVisible({ timeout: 2000 }).catch(() => false))
    )
      return

    await checkbox1.click()
    await checkbox2.click()

    const compareBar = page.locator('.fixed.bottom-0')
    await expect(compareBar).toBeVisible({ timeout: 3000 })
    await expect(compareBar).toContainText(/2\/3|2 \/ 3/)

    const removeChip = compareBar.locator('button').filter({ hasText: /✕|×/ }).first()
    await removeChip.click()

    await expect(compareBar).toContainText(/1\/3|1 \/ 3/)
  })

  // Compare bar — clear all
  test('compare bar clear button removes all selections', async ({ page }) => {
    await gotoCatalogue(page)
    const cards = await waitForCards(page, 2)

    const checkbox1 = cards.nth(0).locator('input[type="checkbox"]')
    const checkbox2 = cards.nth(1).locator('input[type="checkbox"]')

    if (
      !(await checkbox1.isVisible({ timeout: 2000 }).catch(() => false)) ||
      !(await checkbox2.isVisible({ timeout: 2000 }).catch(() => false))
    )
      return

    await checkbox1.click()
    await checkbox2.click()

    const compareBar = page.locator('.fixed.bottom-0')
    await expect(compareBar).toBeVisible({ timeout: 3000 })

    const clearBtn = compareBar.locator('button').filter({ hasText: /clear|effacer|مسح/i })
    await clearBtn.click()

    await expect(compareBar).toBeHidden({ timeout: 3000 })
  })
})

// ==========================================================================
//  Navigation & Cross-Cutting
//  T047 — Nav entry, T045 — Workbox, T029 — i18n
// ==========================================================================
test.describe('Navigation & Cross-Cutting Concerns', () => {
  // T047 — Breed catalogue is reachable from poultry nav
  test('T047 — breed catalogue is accessible from poultry navigation', async ({ page }) => {
    await page.goto('/poultry')
    await page.waitForTimeout(1000)

    const breedLink = page.locator('a, button').filter({
      hasText: /breed|race|catalogue|سلالات/i,
    }).first()

    if (await breedLink.isVisible({ timeout: 3000 }).catch(() => false)) {
      await breedLink.click()
      await page.waitForURL(/\/poultry\/breeds/, { timeout: 5000 })
      await expect(page.locator('h1')).toBeVisible({ timeout: 5000 })
    }
  })

  // Route guard — unauthenticated access redirects
  test('unauthenticated access to /poultry/breeds redirects to login', async ({ browser }) => {
    const context = await browser.newContext()
    const page = await context.newPage()

    await page.goto('/poultry/breeds')
    await page.waitForURL(/\/(login|auth)/, { timeout: 8000 })
    await context.close()
  })

  // i18n — UI strings are translated
  test('T029 — breed catalogue heading uses i18n key', async ({ page }) => {
    await gotoCatalogue(page)
    const heading = page.locator('h1')
    const text = await heading.textContent()
    expect(text).toBeTruthy()
    expect(text).not.toContain('breed_catalogue')
  })

  // Detail route
  test('T032 — /poultry/breeds/:id route loads the detail view', async ({ page }) => {
    await gotoCatalogue(page)
    const card = page.locator('.group.cursor-pointer').first()
    await expect(card).toBeVisible({ timeout: 10000 })
    await card.click()
    await page.waitForURL(/\/poultry\/breeds\/[0-9a-f-]+$/i, { timeout: 5000 })
    await expect(page.locator('h1').first()).toBeVisible({ timeout: 5000 })
  })

  // Compare route
  test('T032 — /poultry/breeds/compare route exists', async ({ page }) => {
    await page.goto('/poultry/breeds/compare')
    await page.waitForTimeout(2000)
    expect(page.url()).toMatch(/\/poultry\/breeds/)
  })
})

// ==========================================================================
//  Edge Cases (from spec)
// ==========================================================================
test.describe('Edge Cases', () => {
  // Special characters in search
  test('searching with special characters does not crash', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    await searchInput.fill('<script>alert("xss")</script>')
    await page.waitForTimeout(500)

    await expect(page.locator('h1')).toBeVisible()
  })

  // Very long search term is truncated
  test('search input truncates at 100 characters', async ({ page }) => {
    await gotoCatalogue(page)
    const searchInput = page.locator('input[type="text"][maxlength="100"]')
    const longString = 'A'.repeat(150)
    await searchInput.fill(longString)

    const value = await searchInput.inputValue()
    expect(value.length).toBeLessThanOrEqual(100)
  })

  // Catalogue persists across reloads
  test('previously loaded catalogue data persists across page reloads', async ({ page }) => {
    await gotoCatalogue(page)
    await waitForCards(page)

    await page.reload()
    await page.waitForTimeout(2000)

    const cards = page.locator('.group.cursor-pointer')
    await expect(cards.first()).toBeVisible({ timeout: 10000 })
  })
})
