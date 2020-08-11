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


/** Writes out relevant details to an event card */
function populateEventContainer(event, containerId) {
  const indexOfEventCard = 25;
  $.get('event-card.html', function(eventCardTotal) {
    const eventCardId = `event-${event.eventId}`;
    const eventCard = $(eventCardTotal).get(indexOfEventCard);
    $(eventCard).attr('id', eventCardId);
    $(`#${containerId}`).append(eventCard);
    $(`#${eventCardId} #event-card-title`).html(event.name);
    $(`#${eventCardId} #event-card-description`).html(event.description);
    $(`#${eventCardId} #event-card-date`)
        .html(buildDate(event.date.year, event.date.month, event.date.dayOfMonth));
    $(`#${eventCardId} #event-card-time`).html(event.time);
    $(`#${eventCardId} #event-card-location`).html(event.location);
    $(`#${eventCardId} #event-card-volunteers`)
        .html(buildVolunteers(event.opportunities));
    buildAsLabels(eventCardId, event.labels, 'interests');
    buildSkillsAsLabels(eventCardId, event.opportunities);
    addLinkToRegister(eventCardId);
    addLinkToDetails(eventCardId);
  });
}

/** Adds a hyperlink to the registration button of event card */
function addLinkToRegister(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' .btn-primary #event-register')
      .attr('href', `/event-details.html?eventId=${eventId}&register=true`);
}

/** Adds a hyperlink to the details button of event card */
function addLinkToDetails(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' .btn #event-details')
      .attr('href', `/event-details.html?eventId=${eventId}&register=false`);
}

function buildDate(year, month, dayOfMonth) {
  return month + '/' + dayOfMonth + '/' + year;
}

/** Creates a properly-formatted volunteering opportunity string */
function buildVolunteers(opportunities) {
  let opportunityString = '';
  for (const opportunity of opportunities) {
    opportunityString += opportunity.name + ', ';
  }
  return opportunityString.slice(0, -2);
}

/** Creates a button label for each provided interest or skill */
function buildAsLabels(eventCardId, labels, className) {
  for (const label of labels) {
    const newLabelButton = document.createElement('button');
    newLabelButton.classList.add(`btn-${className}`);
    newLabelButton.classList.add('btn');
    newLabelButton.disabled = true;
    newLabelButton.innerHTML = label;
    document
        .querySelector(`#${eventCardId} #event-card-labels`)
        .appendChild(newLabelButton);
  }
}

/** Creates the corresponding skill button labels for each volunteering opportunity */
function buildSkillsAsLabels(eventId, opportunities) {
  for (const opportunity of opportunities) {
    buildAsLabels(eventId, opportunity.requiredSkills, 'skills');
  }
}
