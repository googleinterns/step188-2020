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

/*
 * Checks login status and shows comment form, and logout prompt if
 * user is logged in, and shows login prompt if user is not logged in.
 */
function checkLoginStatus() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        const isLoggedIn = loginStatus.isLoggedIn;
        if (isLoggedIn) {
          $('#header').load('header-logged-in.html', function() {
            $('.active').removeClass('active');
            const currentPageArr = window.location.href.split(/[/|.]/);
            const currentPage = currentPageArr[currentPageArr.length - 2];
            if (currentPage === 'profile') {
              $('#profile-header').addClass('active');
            } else if (currentPage === 'events-feed') {
              $('#feed-header').addClass('active');
            } else {
              $('#home-header').addClass('active');
            }
            fetchLogoutUrl();
          });
        } else {
            window.href = 'index.html';
        }
      });
}

function fetchLogoutUrl() {
  fetch('/logout-url')
      .then((response) => {
        return response.text();
      })
      .then((logoutUrl) => {
        const logoutPrompt = document.getElementById('logout-prompt');
        logoutPrompt.href = logoutUrl;
      });
}