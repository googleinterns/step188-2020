window.onload = function onLoad() {
  addLoginUrlToButton();
};

/**
 * Adds login url to login button.
 */
function addLoginUrlToButton() {
  fetch('/login-url')
      .then((response) => {
        return response.text();
      })
      .then((loginUrl) => {
        const loginPrompt = document.getElementById('login-prompt');
        loginPrompt.href = loginUrl;
      });
}
