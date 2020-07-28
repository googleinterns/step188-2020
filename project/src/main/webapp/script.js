// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

window.onload = function onLoad() {
  checkLoginStatus();
};

/**
 * Checks login status and displays navbar and profile if user is logged in,
 * and redirects to index page if user is not logged in.
 */
function checkLoginStatus() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        const isLoggedIn = loginStatus.isLoggedIn;
        if (isLoggedIn) {
          loadHeader();
        } else {
          window.href = 'index.html';
        }
      });
}

/**
 * Loads header and displays the page corresponding to the current url.
 */
function loadHeader() {
  $('#header').load('header.html', function() {
    $('.active').removeClass('active');
    const currentPageArr = window.location.href.split(/[/|.]/);
    const currentPage = currentPageArr[currentPageArr.length - 2];
    if (currentPage === 'profile') {
      $('#profile-header').addClass('active');
    } else if (currentPage === 'events-feed') {
      $('#feed-header').addClass('active');
    }
    addLogoutUrlToButton();
  });
}

/**
 * Adds the logout url to logout button.
 */
function addLogoutUrlToButton() {
  fetch('/logout-url')
      .then((response) => {
        return response.text();
      })
      .then((logoutUrl) => {
        const logoutPrompt = document.getElementById('logout-prompt');
        logoutPrompt.href = logoutUrl;
      });
}
