async function isLoggedIn() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  return loginStatus.isLoggedIn;
}
