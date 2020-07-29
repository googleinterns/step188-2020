window.onload = function onLoad() {
  addLoginUrlToButton();
};

/**
 * Adds login url to login button.
 */
async function addLoginUrlToButton() {
  const response = await fetch('/login-url');
  const loginUrl = await response.text();

  const loginPrompt = document.getElementById('login-prompt');
  loginPrompt.href = loginUrl;
}
