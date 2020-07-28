window.onload = function onLoad() {
  fetchLoginUrl();
};

/**
 * Add login url to login button
 */ 
function addLoginUrl() {
  fetch('/login-url')
      .then((response) => {
        return response.text();
      })
      .then((loginUrl) => {
        const loginPrompt = document.getElementById('login-prompt');
        loginPrompt.href = loginUrl;
      });
}