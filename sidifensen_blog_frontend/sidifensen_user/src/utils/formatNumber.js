export const formatCompactNumber = (value) => {
  const numericValue = Number(value) || 0;
  const absoluteValue = Math.abs(numericValue);

  if (absoluteValue < 1000) {
    return `${Math.round(numericValue)}`;
  }

  if (absoluteValue < 1000000) {
    return formatWithUnit(numericValue, 1000, "K");
  }

  return formatWithUnit(numericValue, 1000000, "M");
};

const formatWithUnit = (value, divisor, unit) => {
  const scaledValue = value / divisor;
  const decimals = Math.abs(scaledValue) >= 100 ? 0 : 1;
  const formattedValue = scaledValue.toFixed(decimals).replace(/\.0$/, "");
  return `${formattedValue}${unit}`;
};
