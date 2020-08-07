window.onload = function onLoad() {
  redirectIfLoggedIn();
};

async function redirectIfLoggedIn() {
  const loggedIn = await isLoggedIn();
  if (loggedIn) {
    window.location.href = 'events-feed.html';
  } else {
    addLoginUrlToElement('login-prompt');
    addLoginUrlToElement('explore-link');
  }
}

async function addLoginUrlToElement(id) {
  const loginUrl = await getLoginUrl();
  const element = document.getElementById(id);
  element.href = loginUrl;
}

async function getLoginUrl() {
  const response = await fetch('/login-url');
  const loginUrl = await response.text();
  return loginUrl;
}
