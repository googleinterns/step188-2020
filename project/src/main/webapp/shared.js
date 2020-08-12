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

/** Adds relevant tags to input box when corresponding option is clicked */
function populatePrefilled(elementId) {
  $('#prefilled-' + elementId).hide();
  getPrefilledInformation(elementId).then((prefilledInfo) => {
    for (const info of prefilledInfo) {
      // Build the option
      const newTag = buildTag(elementId);
      newTag.querySelector('.btn-' + elementId).textContent = info;
      newTag.querySelector('.adder').addEventListener('click', function() {
        moveTagsFromPoolToInput(this, '#' + elementId);
      });
      $('#prefilled-' + elementId).append(newTag);
    }
  })
  
}

async function moveTagsFromPoolToInput(clickedAdder, tagId) {
  addTagsToInput(clickedAdder, tagId);
  removeTagsFromPool(clickedAdder);
}

/** Add tag to user input box once it is chosen */
function addTagsToInput(clickedAdder, tagId) {
  $.getScript('https://cdnjs.cloudflare.com/ajax/libs/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.js', function() {
    $(tagId).on('itemAdded', function() {
      if ($(tagId).prevAll().length > 2) {
        const otherId = tagId === '#interests' ? '#skills' : '#interests';
        $(otherId).prev().remove();
        $(tagId).prev().prev().remove();
      }
    });
    $(tagId).on('itemRemoved', function(event) {
      addTagBackToPool(event.item, tagId);
    });
    $(tagId).tagsinput('refresh');
    $(tagId).tagsinput('add', $(clickedAdder).next().html());
  });
}

/** Puts item back into pool if it was a preset */
async function addTagBackToPool(item, tagId) {
  const prefilledItems = await getPrefilledInformation(tagId);
  if (prefilledItems.includes(item)) {
    $(tagId + '-div > .prefilled-pool > .btn-group:contains("' + item + '")').show();
  }
}

/** Remove tag from pool of options once it is chosen */
function removeTagsFromPool(clickedAdder) {
  $(clickedAdder).parent().hide();
}

function togglePrefilledSkills() {
  $('#prefilled-skills').toggle();
}

function togglePrefilledInterests() {
  $('#prefilled-interests').toggle();
}
