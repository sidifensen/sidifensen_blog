// Vitest setup file for handling CSS imports
import { vi } from 'vitest'

// Mock CSS imports
vi.mock('*.css', () => ({}))
vi.mock('*.scss', () => ({}))
vi.mock('*.sass', () => ({}))

// Mock element-plus CSS
vi.mock('element-plus/theme-chalk/base.css', () => ({}))
vi.mock('element-plus/theme-chalk/index.css', () => ({}))

// Global test utilities
global.beforeEach = beforeEach
global.afterEach = afterEach
