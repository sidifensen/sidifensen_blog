module.exports = {
  extends: ['@commitlint/config-conventional'],

  rules: {
    // 提交类型: feat, fix, docs, style, refactor, perf, test, chore, revert
    'type-enum': [
      2,
      'always',
      [
        'feat',     // 新功能
        'fix',      // 修复bug
        'docs',     // 文档变更
        'style',    // 代码格式（不影响功能）
        'refactor', // 重构
        'perf',     // 性能优化
        'test',     // 测试相关
        'build',    // 构建相关
        'ci',       // CI/CD相关
        'chore',    // 其他更改
        'revert',   // 回滚
      ],
    ],

    // type 为空时报错
    'type-empty': [2, 'never'],

    // type 后面需要空一格
    'type-case': [2, 'always', 'lower-case'],

    // subject 不能为空
    'subject-empty': [2, 'never'],

    // subject 结尾禁止句号
    'subject-full-stop': [2, 'never', '.'],

    // subject 最大长度
    'subject-max-length': [2, 'always', 100],

    // body 最大行数
    'body-max-line-length': [2, 'always', 100],
  },

  // 忽略包含这些关键字的提交（用于合并提交等）
  ignores: [(commit) => commit.includes('Merge')],

  // 提交消息正则（备用验证）
  defaultIgnores: true,
};
