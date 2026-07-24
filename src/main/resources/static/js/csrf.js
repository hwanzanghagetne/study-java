async function fetchWithCsrf(url, options = {}) {
  const token = document.cookie.split("; ").find((c) => c.startsWith("XSRF-TOKEN=")).split("=")[1];
  options.headers = { ...options.headers, "X-XSRF-TOKEN": token };
  return fetch(url, options);
}
