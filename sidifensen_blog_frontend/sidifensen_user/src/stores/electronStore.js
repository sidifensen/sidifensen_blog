import { defineStore } from 'pinia';

const HISTORY_KEY = 'electron_reading_history';
const MAX_HISTORY = 100;

export const useElectronStore = defineStore('electron', {
  state: () => ({
    readingHistory: JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]'),
  }),
  actions: {
    addToHistory(article) {
      const history = this.readingHistory.filter(item => item.id !== article.id);
      history.unshift({
        id: article.id,
        title: article.title,
        cover: article.cover,
        readAt: Date.now(),
      });
      if (history.length > MAX_HISTORY) {
        history.pop();
      }
      this.readingHistory = history;
      localStorage.setItem(HISTORY_KEY, JSON.stringify(history));
    },
    clearHistory() {
      this.readingHistory = [];
      localStorage.removeItem(HISTORY_KEY);
    },
  },
});
