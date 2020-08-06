async function isLoggedIn() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  return loginStatus.loginState === 'LOGGED_IN';
}

async function getPrefilledInformation(informationCategory) {
  const response = 
      await fetch('/prefilled-information?' + new URLSearchParams({'category': informationCategory}))
  return response.json();
}

function getPrefilledInterests() {
  return getPrefilledInformation('interests');
}

function getPrefilledSkills() {
  return getPrefilledInformation('skills');
}

function buildTag(tagClass) {
  const group = document.createElement('div');
  group.classList.add('btn-group');
  group.setAttribute('role', 'group');

  const addButton = document.createElement('button');
  addButton.setAttribute('type', 'button');
  addButton.classList.add('btn', 'btn-secondary', 'adder', tagClass);
  addButton.textContent = '+';

  const tag = document.createElement('button');
  tag.setAttribute('type', 'button');
  tag.disabled = true;
  tag.classList.add('btn', 'btn-secondary', 'btn-' + tagClass);
  tag.classList.add(tagClass);

  group.appendChild(addButton);
  group.appendChild(tag);
  return group;
}

function buildInterestTag() {
  return buildTag('interest');
}
function buildSkillTag() {
  return buildTag('skill');
}

function buildOption(val) {
  const option = document.createElement('option');
  option.defaultSelected = true;
  option.value = val;
  option.innerHTML = val;
  return option;
}

function buildSkillSpan(val) {
  const span = document.createElement('span');
  const innerSpan = document.createElement('span');
  innerSpan.setAttribute('data-role', 'remove');
  span.classList.add('tag', 'label', 'label-info');
  span.innerHTML = val;
  span.appendChild(innerSpan);
  return span;
}
