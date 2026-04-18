<template>
  <div class="hero-cosmic-ring" aria-hidden="true">
    <div class="ambient ambient-1"></div>
    <div class="ambient ambient-2"></div>
    <div class="ambient ambient-3"></div>

    <div class="orbit orbit-back">
      <span class="orbit-node node-a"></span>
      <span class="orbit-node node-b"></span>
    </div>
    <div class="orbit orbit-mid">
      <span class="orbit-node node-c"></span>
    </div>
    <div class="orbit orbit-front">
      <span class="orbit-node node-d"></span>
      <span class="orbit-node node-e"></span>
    </div>

    <div class="focus-card">
      <div class="focus-card__eyebrow">社区信号</div>
      <div class="focus-card__title">构建、分享、持续迭代</div>

      <div class="focus-card__grid">
        <div class="metric">
          <span class="metric__label">前端</span>
          <span class="metric__value">Vue 3</span>
        </div>
        <div class="metric">
          <span class="metric__label">后端</span>
          <span class="metric__value">Spring</span>
        </div>
        <div class="metric">
          <span class="metric__label">数据</span>
          <span class="metric__value">MySQL</span>
        </div>
        <div class="metric">
          <span class="metric__label">状态</span>
          <span class="metric__value">在线</span>
        </div>
      </div>
    </div>

    <div class="satellite satellite-top">工程实践</div>
    <div class="satellite satellite-right">开源共创</div>
    <div class="satellite satellite-bottom">创意编码</div>

    <span class="spark spark-1"></span>
    <span class="spark spark-2"></span>
    <span class="spark spark-3"></span>
    <span class="spark spark-4"></span>
    <span class="spark spark-5"></span>
    <span class="spark spark-6"></span>
  </div>
</template>

<style lang="scss" scoped>
.hero-cosmic-ring {
  --ring-blue: #1677ff;
  --ring-cyan: #4cc9ff;
  --ring-indigo: #3158d3;
  --ring-ink: rgba(10, 18, 34, 0.86);
  --ring-line: rgba(22, 119, 255, 0.2);
  --ring-card: rgba(255, 255, 255, 0.68);
  --ring-card-border: rgba(255, 255, 255, 0.72);
  --ring-shadow: 0 30px 80px rgba(42, 79, 163, 0.18);
  --ring-text: #10203d;
  --ring-text-soft: rgba(16, 32, 61, 0.6);

  position: absolute;
  right: 3%;
  top: 50%;
  width: 560px;
  height: 560px;
  transform: translateY(-50%);
  pointer-events: none;
  z-index: 1;
  filter: saturate(1.05);

  @media (max-width: 1280px) {
    right: -2%;
    transform: translateY(-50%) scale(0.9);
  }

  @media (max-width: 1200px) {
    display: none;
  }
}

.ambient {
  position: absolute;
  border-radius: 999px;
  filter: blur(12px);
  animation: ambientFloat 12s ease-in-out infinite;
}

.ambient-1 {
  inset: 100px 70px 160px 140px;
  background:
    radial-gradient(circle at 35% 35%, rgba(76, 201, 255, 0.28), transparent 48%),
    radial-gradient(circle at 60% 60%, rgba(49, 88, 211, 0.16), transparent 58%);
}

.ambient-2 {
  top: 85px;
  right: 85px;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(22, 119, 255, 0.22), transparent 70%);
  animation-delay: -4s;
}

.ambient-3 {
  left: 90px;
  bottom: 90px;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(76, 201, 255, 0.16), transparent 72%);
  animation-delay: -7s;
}

.orbit {
  position: absolute;
  left: 50%;
  top: 50%;
  border-radius: 50%;
  transform-style: preserve-3d;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    border: 1px solid transparent;
    background: linear-gradient(
        135deg,
        rgba(255, 255, 255, 0.78),
        rgba(255, 255, 255, 0.05) 32%,
        rgba(22, 119, 255, 0.22) 60%,
        rgba(76, 201, 255, 0.4)
      )
      border-box;
    -webkit-mask:
      linear-gradient(#fff 0 0) padding-box,
      linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    opacity: 0.88;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 10%;
    border-radius: inherit;
    border: 1px solid rgba(255, 255, 255, 0.18);
    opacity: 0.5;
  }
}

.orbit-back {
  width: 410px;
  height: 250px;
  margin-left: -205px;
  margin-top: -125px;
  transform: translate3d(0, -12px, 0) rotate(-18deg) rotateX(69deg);
  animation: orbitSpin 18s linear infinite;
}

.orbit-mid {
  width: 332px;
  height: 202px;
  margin-left: -166px;
  margin-top: -101px;
  transform: translate3d(18px, 10px, 0) rotate(28deg) rotateX(71deg);
  animation: orbitSpinReverse 15s linear infinite;
}

.orbit-front {
  width: 470px;
  height: 290px;
  margin-left: -235px;
  margin-top: -145px;
  transform: translate3d(0, 14px, 0) rotate(12deg) rotateX(67deg);
  animation: orbitSpin 24s linear infinite;
  opacity: 0.74;
}

.orbit-node {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: radial-gradient(
    circle at 30% 30%,
    #ffffff 0%,
    var(--ring-cyan) 45%,
    var(--ring-blue) 100%
  );
  box-shadow:
    0 0 0 5px rgba(76, 201, 255, 0.1),
    0 0 20px rgba(76, 201, 255, 0.5);
}

.node-a {
  left: 14%;
  top: 48%;
}

.node-b {
  right: 12%;
  top: 38%;
}

.node-c {
  left: 50%;
  top: 8%;
  width: 14px;
  height: 14px;
  transform: translateX(-50%);
}

.node-d {
  left: 28%;
  bottom: 6%;
  width: 10px;
  height: 10px;
}

.node-e {
  right: 20%;
  top: 12%;
  width: 9px;
  height: 9px;
}

.focus-card {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 274px;
  padding: 26px 24px 22px;
  border-radius: 30px;
  transform: translate(-50%, -50%) rotate(-9deg);
  background:
    linear-gradient(155deg, rgba(255, 255, 255, 0.86), rgba(255, 255, 255, 0.32)),
    radial-gradient(circle at top right, rgba(76, 201, 255, 0.22), transparent 42%);
  border: 1px solid var(--ring-card-border);
  box-shadow:
    var(--ring-shadow),
    inset 0 1px 0 rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  overflow: hidden;
  animation: cardDrift 9s ease-in-out infinite;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      linear-gradient(130deg, rgba(255, 255, 255, 0.42), transparent 34%),
      linear-gradient(315deg, rgba(22, 119, 255, 0.1), transparent 55%);
    pointer-events: none;
  }
}

.focus-card__eyebrow,
.focus-card__title,
.focus-card__grid {
  position: relative;
  z-index: 1;
}

.focus-card__eyebrow {
  margin-bottom: 12px;
  font-size: 11px;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: var(--ring-text-soft);
}

.focus-card__title {
  max-width: 190px;
  margin-bottom: 20px;
  font-size: 28px;
  line-height: 1.05;
  font-weight: 700;
  letter-spacing: -0.04em;
  color: var(--ring-text);
}

.focus-card__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.metric {
  padding: 12px 12px 11px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.42);
  border: 1px solid rgba(255, 255, 255, 0.54);
}

.metric__label {
  display: block;
  margin-bottom: 6px;
  font-size: 11px;
  color: var(--ring-text-soft);
}

.metric__value {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: var(--ring-text);
}

.satellite {
  position: absolute;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  padding: 0 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 16px 38px rgba(19, 51, 112, 0.12);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(16, 32, 61, 0.72);
}

.satellite-top {
  top: 112px;
  left: 112px;
  animation: chipFloat 7s ease-in-out infinite;
}

.satellite-right {
  top: 194px;
  right: 62px;
  animation: chipFloat 8s ease-in-out infinite -2s;
}

.satellite-bottom {
  left: 148px;
  bottom: 86px;
  animation: chipFloat 7.4s ease-in-out infinite -3.5s;
}

.spark {
  position: absolute;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 14px rgba(255, 255, 255, 0.7);
  animation: sparkBlink 4.8s ease-in-out infinite;
}

.spark-1 {
  left: 72px;
  top: 154px;
}

.spark-2 {
  right: 126px;
  top: 98px;
  animation-delay: -0.8s;
}

.spark-3 {
  right: 90px;
  top: 318px;
  animation-delay: -2.1s;
}

.spark-4 {
  left: 120px;
  bottom: 126px;
  animation-delay: -1.2s;
}

.spark-5 {
  left: 258px;
  top: 72px;
  animation-delay: -3.2s;
}

.spark-6 {
  right: 186px;
  bottom: 70px;
  animation-delay: -2.6s;
}

@keyframes orbitSpin {
  from {
    transform: translate3d(0, -12px, 0) rotate(-18deg) rotateX(69deg) rotateZ(0deg);
  }
  to {
    transform: translate3d(0, -12px, 0) rotate(-18deg) rotateX(69deg) rotateZ(360deg);
  }
}

@keyframes orbitSpinReverse {
  from {
    transform: translate3d(18px, 10px, 0) rotate(28deg) rotateX(71deg) rotateZ(0deg);
  }
  to {
    transform: translate3d(18px, 10px, 0) rotate(28deg) rotateX(71deg) rotateZ(-360deg);
  }
}

@keyframes ambientFloat {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -12px, 0) scale(1.04);
  }
}

@keyframes cardDrift {
  0%,
  100% {
    transform: translate(-50%, -50%) rotate(-9deg);
  }
  50% {
    transform: translate(-50%, calc(-50% - 10px)) rotate(-6deg);
  }
}

@keyframes chipFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes sparkBlink {
  0%,
  100% {
    opacity: 0.35;
    transform: scale(0.85);
  }
  50% {
    opacity: 1;
    transform: scale(1.4);
  }
}

html.dark {
  .hero-cosmic-ring {
    --ring-line: rgba(76, 201, 255, 0.18);
    --ring-card: rgba(12, 18, 30, 0.62);
    --ring-card-border: rgba(140, 182, 255, 0.18);
    --ring-shadow: 0 32px 84px rgba(0, 0, 0, 0.44);
    --ring-text: #edf5ff;
    --ring-text-soft: rgba(237, 245, 255, 0.58);
  }

  .orbit::before {
    background: linear-gradient(
        135deg,
        rgba(180, 221, 255, 0.34),
        rgba(255, 255, 255, 0.02) 28%,
        rgba(22, 119, 255, 0.26) 56%,
        rgba(76, 201, 255, 0.34)
      )
      border-box;
  }

  .orbit::after {
    border-color: rgba(255, 255, 255, 0.08);
  }

  .focus-card {
    background:
      linear-gradient(155deg, rgba(18, 30, 52, 0.94), rgba(8, 14, 24, 0.74)),
      radial-gradient(circle at top right, rgba(76, 201, 255, 0.22), transparent 42%);
    border-color: rgba(122, 165, 255, 0.18);
    box-shadow:
      var(--ring-shadow),
      inset 0 1px 0 rgba(255, 255, 255, 0.06);
  }

  .metric,
  .satellite {
    background: rgba(13, 21, 35, 0.6);
    border-color: rgba(255, 255, 255, 0.08);
  }

  .satellite {
    color: rgba(237, 245, 255, 0.74);
    box-shadow: 0 20px 48px rgba(0, 0, 0, 0.3);
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-cosmic-ring *,
  .hero-cosmic-ring::before,
  .hero-cosmic-ring::after {
    animation: none !important;
    transition: none !important;
  }
}
</style>
